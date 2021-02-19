package com.deviget.minesweeperAPI.util;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.Position;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;

public class GridDrawer {

    public static void draw(Board board) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();
        System.out.print("\nValues:\n");
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                Cell cell = board.getGrid().get(new Position(row, col));
                if (cell.isMined())
                    System.out.print(" *");
                else
                    System.out.print(" " + cell.getMinesAround());
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Interface:");
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                Cell cell = board.getGrid().get(new Position(row, col));
                if (cell.isVisible()) {
                    if (cell.isMined())
                        System.out.print(" *");
                    else if (cell.getMinesAround() > 0)
                        System.out.print(" " + cell.getMinesAround());
                    else
                        System.out.print(" 0");
                }
                if (cell.isHidden())
                    System.out.print(" #");
                if (cell.isFlagged())
                    System.out.print(" F");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Status: " + board.getStatus().getValue().toUpperCase() + "!\n");
    }
}
