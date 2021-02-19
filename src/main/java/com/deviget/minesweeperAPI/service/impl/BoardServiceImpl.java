package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.error.BadRequestException;
import com.deviget.minesweeperAPI.error.NotFoundException;
import com.deviget.minesweeperAPI.lock.LockService;
import com.deviget.minesweeperAPI.repository.BoardRepository;
import com.deviget.minesweeperAPI.service.BoardService;
import com.deviget.minesweeperAPI.service.GameEngineService;
import com.deviget.minesweeperAPI.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class BoardServiceImpl implements BoardService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;
    @Autowired
    private GameEngineService gameEngineService;
    @Autowired
    private LockService lockService;
    @Autowired
    private BoardRepository boardRepository;

    /**
     *
     * @param username
     * @param rowSize
     * @param colSize
     * @param minesAmount
     * @return
     */
    @Override
    public Board createBoard(String username, int rowSize, int colSize, int minesAmount) {

        logger.info(String.format("createBoard params (username: %s, rowSize: %s, colSize: %s, minesAmount: %s)", username, rowSize, colSize, minesAmount));
        validateMinesAmount(minesAmount, rowSize, colSize);

        User user = userService.getUserByUsername(username);
        Board board = Board.buildBoard(user, rowSize, colSize, minesAmount);
        logger.info(String.format("board builded: %s", board.toString()));

        gameEngineService.initializeBoardScenario(board);
        logger.info(String.format("board scenario generated: %s", board.toString()));

        return saveBoard(board);
    }

    /**
     *
     * @param username
     * @param boardId
     * @param rowNumber
     * @param colNumber
     * @return
     */
    @Override
    public Board revealCell(String username, String boardId, int rowNumber, int colNumber) {

        Board board = getBoardByIdAndUsername(boardId, username);
        validateCoordinates(rowNumber, colNumber, board);
        if (board.wasWon() || board.wasLost())
            return board;

        //locks board using try with resources
//        try (BoardLock lock = lockService.lock(board)) {
        gameEngineService.revealCell(board, rowNumber, colNumber);
//        }
        logger.info(String.format("board after revealCell: %s", board.toString()));

        return saveBoard(board);
    }

    /**
     *
     * @param username
     * @param boardId
     * @param rowNumber
     * @param colNumber
     * @return
     */
    @Override
    public Board flagCell(String username, String boardId, int rowNumber, int colNumber) {

        Board board = getBoardByIdAndUsername(boardId, username);
        validateCoordinates(rowNumber, colNumber, board);
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

        User user = userService.getUserByUsername(username);
        Board board = boardRepository.findByIdAndUserId(id, user.getId());
        if (isNull(board))
            throw new NotFoundException(String.format("Board '%s' not found", id));
        logger.info(String.format("board finded: %s", board.toString()));
        return board;
    }

    /**
     *
     * @param board
     * @return
     */
    @Override
    public Board saveBoard(Board board) {

        return boardRepository.save(board);
    }

    /**
     *
     * @param mines
     * @param rows
     * @param cols
     */
    private void validateMinesAmount(int mines, int rows, int cols) {

        int boardSize = rows * cols;
        if (mines > boardSize)
            throw new BadRequestException(String.format("minesAmount should not be bigger than %s", boardSize));
    }

    /**
     *
     * @param row
     * @param col
     * @param board
     */
    private void validateCoordinates(int row, int col, Board board) {

        if (row >= board.getRowSize())
            throw new BadRequestException(String.format("rowNumber should be less than %s", board.getRowSize()));
        if (col >= board.getColSize())
            throw new BadRequestException(String.format("colNumber should be less than %s", board.getColSize()));
    }
}
