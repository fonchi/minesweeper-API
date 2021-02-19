package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.Position;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.error.BadRequestException;
import com.deviget.minesweeperAPI.error.NotFoundException;
import com.deviget.minesweeperAPI.lock.LockService;
import com.deviget.minesweeperAPI.repository.BoardRepository;
import com.deviget.minesweeperAPI.service.BoardService;
import com.deviget.minesweeperAPI.service.UserService;
import com.deviget.minesweeperAPI.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static java.util.Objects.isNull;

@Service
public class BoardServiceImpl implements BoardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;
    @Autowired
    private LockService lockService;
    @Autowired
    private BoardRepository boardRepository;

    @Override
    public Board createBoard(String username, int rowSize, int colSize, int minesAmount) {

        logger.info(String.format("createBoard params (username: %s, rowSize: %s, colSize: %s, minesAmount: %s)", username, rowSize, colSize, minesAmount));
        validateMinesAmount(minesAmount, rowSize, colSize);

        User user = getUser(username);
        Board board = buildBoard(user, rowSize, colSize, minesAmount);
        logger.info(String.format("board builded: %s", board.toString()));

        initializeGrid(board);
        logger.info(String.format("board initialized: %s", board.toString()));

        return saveBoard(board);
    }

    @Override
    public Board revealCell(String username, String boardId, int rowNumber, int colNumber) {

        Board board = getBoard(boardId, getUser(username));
        validateCoordinates(rowNumber, colNumber, board);

        //locks board using try with resources
//        try (BoardLock lock = lockService.lock(board)) {
        if (board.wasWon() || board.wasLost())
            return board;
        board.start();
        Position selectedPos = new Position(rowNumber, colNumber);
        revealCell(board, selectedPos);
//        }
        logger.info(String.format("board after revealCell: %s", board.toString()));

        return saveBoard(board);
    }

    @Override
    public Board flagCell(String username, String boardId, int rowNumber, int colNumber) {

        Board board = getBoard(boardId, getUser(username));
        validateCoordinates(rowNumber, colNumber, board);

        if (rowNumber >= board.getRowSize())
            throw new BadRequestException(String.format("rowNumber should be less than %s", board.getRowSize()));
        if (colNumber >= board.getColSize())
            throw new BadRequestException(String.format("colNumber should be less than %s", board.getColSize()));

        if (board.wasWon() | board.wasLost())
            return board;

        //locks board using try with resources
//        try (BoardLock lock = lockService.lock(board)) {
        Cell cell = board.getGrid().get(new Position(rowNumber, colNumber));
        if (!cell.isFlagged()) {
            cell.setStatus(CellStatusEnum.FLAGGED);
            saveBoard(board);
        }
//        }

        return board;
    }

    /**
     * finds board by id and username
     *
     * @param id
     * @return
     */
    @Override
    public Board getBoardByIdAndUsername(String id, String username) {
        User user = getUser(username);
        return boardRepository.findByIdAndUserId(id, user.getId());
    }

    @Override
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    /**
     * builds board based on configuration params
     *
     * @param user
     * @param rowSize
     * @param colSize
     * @param minesAmount
     * @return
     */
    private Board buildBoard(User user, int rowSize, int colSize, int minesAmount) {

        return Board.builder()
                .id(UniqueIdGenerator.generate())
                .user(user)
                .rowSize(rowSize)
                .colSize(colSize)
                .minesAmount(minesAmount)
                .creationDatetime(Instant.now())
                .status(BoardStatusEnum.NEW)
                .grid(createGrid(rowSize, colSize))
                .build();
    }

    /**
     * creates rowSize x colSize grid with hidden cells
     *
     * @param rowSize
     * @param colSize
     * @return
     */
    private Map<Position, Cell> createGrid(int rowSize, int colSize) {

        Map<Position, Cell> grid = new LinkedHashMap<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                Position pos = new Position(i, j);
                grid.put(pos, Cell.newHidden(pos));
            }
        }
        return grid;
    }

    /**
     * places random mines and calculates mines around of cell
     *
     * @param board
     */
    private void initializeGrid(Board board) {

        int minesPlaced = 0;
        Random random = new Random();
        while (minesPlaced < board.getMinesAmount()) {
            //generate a random number between 0 and size - 1
            int randomRow = random.nextInt(board.getRowSize());
            int randomCol = random.nextInt(board.getColSize());
            Position position = new Position(randomRow, randomCol);
            Cell cell = board.getGrid().get(position);
            if (cell.isMined())
                continue;
            cell.setMined(true);
            cell.setMinesAround(0);
            calculateMinesAround(board, position);
            minesPlaced++;
        }
    }

    /**
     * calculates mines around of cell
     *
     * @param board
     * @param position
     */
    private void calculateMinesAround(Board board, Position position) {

        for (int nearRow = Math.max(0, position.getRow() - 1); nearRow <= Math.min(position.getRow() + 1, board.getRowSize() - 1); nearRow++) {
            for (int nearCol = Math.max(0, position.getCol() - 1); nearCol <= Math.min(position.getCol() + 1, board.getColSize() - 1); nearCol++) {
                Position nearPosition = new Position(nearRow, nearCol);
                if (!board.getGrid().get(nearPosition).isMined()) {
                    board.getGrid().get(nearPosition).incrementMinesAround();
                }
            }
        }
    }

    /**
     * recursive function to reveal selected cell and adjacents if they applied
     *
     * @param board
     * @param position
     */
    private void revealCell(Board board, Position position) {

        Cell cell = board.getGrid().get(position);

        //validates if cell was already revealed or flagged for idempotency response
        if (cell.isVisible() || cell.isFlagged())
            return;

        cell.setStatus(CellStatusEnum.VISIBLE);

        //validates if cell is mined then Game Over!
        if (cell.isMined()) {
            board.setStatus(BoardStatusEnum.LOST);
            board.setFinishDatetime(Instant.now());
            return;
        }

        board.incrementRevealedMines();

        //validates if all cells was revealed then Won Game!
        if (board.wasAllCellsRevealed()) {
            board.setStatus(BoardStatusEnum.WON);
            return;
        }

        //validates if cell contains mines near then return
        if (cell.getMinesAround() > 0)
            return;

        for (int nearRow = Math.max(0, position.getRow() - 1); nearRow <= Math.min(position.getRow() + 1, board.getRowSize() - 1); nearRow++) {
            for (int nearCol = Math.max(0, position.getCol() - 1); nearCol <= Math.min(position.getCol() + 1, board.getColSize() - 1); nearCol++) {
                Position nearPosition = new Position(nearRow, nearCol);
                revealCell(board, nearPosition);
            }
        }
    }

    private User getUser(String username) {
        return userService.getUserByUsername(username);
    }

    private Board getBoard(String boardId, User user) {

        Board board = getBoardByIdAndUsername(boardId, user.getId());
        if (isNull(board))
            throw new NotFoundException(String.format("Board '%s' not found", boardId));
        logger.info(String.format("board finded: %s", board.toString()));

        return board;
    }

    private void validateMinesAmount(int mines, int rows, int cols) {
        int boardSize = rows * cols;
        if (mines > boardSize)
            throw new BadRequestException(String.format("minesAmount should not be bigger than %s", boardSize));
    }

    private void validateCoordinates(int row, int col, Board board) {
        if (row >= board.getRowSize())
            throw new BadRequestException(String.format("rowNumber should be less than %s", board.getRowSize()));
        if (col >= board.getColSize())
            throw new BadRequestException(String.format("colNumber should be less than %s", board.getColSize()));
    }
}
