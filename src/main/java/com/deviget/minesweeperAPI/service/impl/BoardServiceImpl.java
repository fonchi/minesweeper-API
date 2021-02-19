package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
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
        revealCell(board, rowNumber, colNumber);
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
        String cellKey = Cell.getKey(rowNumber, colNumber);
        Cell cell = board.getGrid().get(cellKey);
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
    private Map<String, Cell> createGrid(int rowSize, int colSize) {

        Map<String, Cell> grid = new LinkedHashMap<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                String key = Cell.getKey(i, j);
                grid.put(key, Cell.newHidden(i, j));
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
            String key = Cell.getKey(randomRow, randomCol);
            Cell cell = board.getGrid().get(key);
            if (cell.isMined())
                continue;
            cell.setMined(true);
            cell.setMinesAround(0);
            calculateMinesAround(board, randomRow, randomCol);
            minesPlaced++;
        }
    }

    /**
     * calculates mines around of cell
     *
     * @param board
     * @param row
     * @param col
     */
    private void calculateMinesAround(Board board, int row, int col) {

        for (int nearbyRow = Math.max(0, row - 1); nearbyRow <= Math.min(row + 1, board.getRowSize() - 1); nearbyRow++) {
            for (int nearbyCol = Math.max(0, col - 1); nearbyCol <= Math.min(col + 1, board.getColSize() - 1); nearbyCol++) {
                String nearbyCellKey = Cell.getKey(nearbyRow, nearbyCol);
                Cell nearbyCell = board.getGrid().get(nearbyCellKey);
                if (!nearbyCell.isMined()) {
                    nearbyCell.incrementMinesAround();
                }
            }
        }
    }

    /**
     * recursive function to reveal selected cell and adjacents if they applied
     *
     * @param board
     * @param row
     * @param col
     */
    private void revealCell(Board board, int row, int col) {

        String cellKey = Cell.getKey(row, col);
        Cell cell = board.getGrid().get(cellKey);

        //validates if cell was already revealed or flagged for idempotency response
        if (cell.isVisible() || cell.isFlagged())
            return;

        cell.setStatus(CellStatusEnum.VISIBLE);

        //validates if cell is mined then Game Over!
        if (cell.isMined()) {
            board.finish(BoardStatusEnum.LOST);
            return;
        }

        board.incrementRevealedMines();

        //validates if all cells was revealed then Won Game!
        if (board.wasAllCellsRevealed()) {
            board.finish(BoardStatusEnum.WON);
            return;
        }

        //validates if cell contains mines near then return
        if (cell.getMinesAround() > 0)
            return;

        for (int nearbyRow = Math.max(0, row - 1); nearbyRow <= Math.min(row + 1, board.getRowSize() - 1); nearbyRow++) {
            for (int nearbyCol = Math.max(0, col - 1); nearbyCol <= Math.min(col + 1, board.getColSize() - 1); nearbyCol++) {
                revealCell(board, nearbyRow, nearbyCol);
            }
        }
    }

    private User getUser(String username) {
        return userService.getUserByUsername(username);
    }

    private Board getBoard(String boardId, User user) {

        Board board = getBoardByIdAndUsername(boardId, user.getUsername());
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
