package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.domain.Board;

public interface GameEngineService {

    void initializeBoardScenario(Board board);

    void revealCell(Board board, int row, int col);

}
