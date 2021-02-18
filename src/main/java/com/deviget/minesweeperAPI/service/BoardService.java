package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.Board;

public interface BoardService {

    Board createBoard(String userId, int rowSize, int colSize, int minesAmount);

    Board revealCell(String userId, String boardId, int rowNumber, int colNumber);

    Board addFlag(String userId, int rowNumber, int colNumber);

    Board getBoard(String id);

    Board saveBoard(Board board);
}
