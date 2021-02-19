package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.Position;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.error.NotFoundException;
import com.deviget.minesweeperAPI.lock.BoardLock;
import com.deviget.minesweeperAPI.lock.LockService;
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

    //temporaly memory var to tests (deprecate after persistency implemented)
    private Board savedBoard;

    @Override
    public Board createBoard(String userId, int rowSize, int colSize, int minesAmount) {

        logger.info(String.format("createBoard params (userId: %s, rowSize: %s, colSize: %s, minesAmount: %s)", userId, rowSize, colSize, minesAmount));

        User user = userService.getUser(userId);
        if (isNull(user))
            throw new NotFoundException(String.format("User '%s' not found", userId));

        logger.info(String.format("user finded:  %s", user.toString()));

        Board board = buildBoard(user, rowSize, colSize, minesAmount);

        logger.info(String.format("board builded: %s", board.toString()));

        initializeGrid(board);

        logger.info(String.format("board initialized: %s", board.toString()));

        board.drawGrid();

        return saveBoard(board);
    }

    @Override
    public Board revealCell(String userId, String boardId, int rowNumber, int colNumber) {

        User user = userService.getUser(userId);
        if (isNull(user))
            throw new NotFoundException(String.format("User '%s' not found", userId));

        logger.info(String.format("user finded:  %s", user.toString()));

        Board board = getBoard(boardId);
        if (isNull(board))
            throw new NotFoundException(String.format("Board '%s' not found", boardId));

        logger.info(String.format("board finded: %s", board.toString()));

        //locks board using try with resources
//        try (BoardLock lock = lockService.lock(board)) {
            board.start();
            Position selectedPos = new Position(rowNumber, colNumber);
            revealCell(board, selectedPos);
//        }

        logger.info(String.format("board after revealCell: %s", board.toString()));

        board.drawGrid();

        return saveBoard(board);
    }

    @Override
    public Board addFlag(String userId, int rowNumber, int colNumber) {
        return null;
    }

    /**
     * finds board by id
     *
     * @param id
     * @return
     */
    @Override
    public Board getBoard(String id) {
        return savedBoard;
    }

    @Override
    public Board saveBoard(Board board) {
        savedBoard = board;
        return board;
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
                .status(BoardStatusEnum.CREATED)
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
                grid.put(new Position(i, j), Cell.newHidden());
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

        //validates if cell was reveal for idempotency response
        if (CellStatusEnum.VISIBLE.equals(cell.getStatus()))
            return;

        if (CellStatusEnum.FLAGGED.equals(cell.getStatus()))
            return;

        cell.setStatus(CellStatusEnum.VISIBLE);

        //validates if cell is mined then Game Over!
        if (cell.isMined()) {
            board.setStatus(BoardStatusEnum.OVER);
            board.setFinishDatetime(Instant.now());
            return;
        }

        board.incrementRevealedMines();

        //validates if all cells was revealed then Won Game!
        if (board.allCellsRevealed()) {
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
}
