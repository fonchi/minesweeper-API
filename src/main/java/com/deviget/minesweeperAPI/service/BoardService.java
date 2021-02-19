package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.Board;

public interface BoardService {

    Board createBoard(String username, int rowSize, int colSize, int minesAmount);

    Board revealCell(String username, String boardId, int rowNumber, int colNumber);

    Board flagCell(String username, String boardId, int rowNumber, int colNumber);

    Board getBoardByIdAndUsername(String id, String username);

    Board saveBoard(Board board);
}
