package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.Board;

public interface BoardService {

    Board createBoard(String userId, int rowSize, int colSize, int minesAmount);

    Board revealCell(String userId, String boardId, int rowNumber, int colNumber);

    Board flagCell(String userId, String boardId, int rowNumber, int colNumber);

    Board getBoardByIdAndUserId(String id, String userId);

    Board saveBoard(Board board);
}
