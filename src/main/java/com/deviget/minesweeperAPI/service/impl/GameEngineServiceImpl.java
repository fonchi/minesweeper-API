package com.deviget.minesweeperAPI.service.impl;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.service.GameEngineService;
import com.deviget.minesweeperAPI.util.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameEngineServiceImpl implements GameEngineService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * places random mines and calculates mines around of cell
     *
     * @param board
     */
    @Override
    public void initializeBoardScenario(Board board) {

        int minesPlaced = 0;
        while (minesPlaced < board.getMinesAmount()) {
            //generate a random number between 0 and size - 1
            int randomRow = RandomGenerator.generateRandomInt(board.getRowSize());
            int randomCol = RandomGenerator.generateRandomInt(board.getColSize());
            Cell cell = board.getGridCell(randomRow, randomCol);
            if (cell.isMined())
                continue;
            cell.setMined(true);
            cell.setMinesAround(0);
            calculateMinesAround(board, randomRow, randomCol);
            minesPlaced++;
        }
    }

    /**
     * recursive function to reveal selected cell and adjacents if they applied
     *
     * @param board
     * @param row
     * @param col
     */
    @Override
    public void revealCell(Board board, int row, int col) {

        board.start();
        Cell cell = board.getGridCell(row, col);

        //validates if cell was already revealed or flagged for idempotency response
        if (cell.isVisible() || cell.isFlagged())
            return;

        cell.setStatus(CellStatusEnum.VISIBLE);
        board.incrementRevealedCells();

        //validates if all cells was revealed then Won Game!
        if (board.wereAllCellsRevealed()) {
            board.finish(BoardStatusEnum.WON);
            return;
        }

        //validates if cell is mined then Lost Game!
        if (cell.isMined()) {
            board.finish(BoardStatusEnum.LOST);
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
                Cell nearbyCell = board.getGridCell(nearbyRow, nearbyCol);
                if (!nearbyCell.isMined()) {
                    nearbyCell.incrementMinesAround();
                }
            }
        }
    }

}
