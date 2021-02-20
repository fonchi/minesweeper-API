package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.TestUtils;
import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.util.RandomGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests to game engine service properties verification
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GameEngineServiceTest {

    @Autowired
    GameEngineService gameEngineService;

    private User user;

    @Before
    public void setUp() {
        user = TestUtils.buildUser();
    }

    @Test
    public void givenEmptyBoard_whenInitializeBoardScenario_thenBoardUpdated() {

        //Setup
        Board board = TestUtils.buildEmptyBoard(user);
        Board exceptedBoard = TestUtils.buildInitializedBoard(user);

        //Mock
        //mock random int generation static method to force that first to fourth invocations returns 0,
        //to ensure mine planted in coordinates (0,0) just one time, and next invocations returns 2, to mine planted in (2,2)
        MockedStatic<RandomGenerator> randomGenerator = mockStatic(RandomGenerator.class);
        randomGenerator.when(() -> RandomGenerator.generateRandomInt(anyInt())).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {
                count++;
                if (count < 5)
                    return 0;

                return 2;
            }
        });

        //Execute
        gameEngineService.initializeBoardScenario(board);

        //Verify
        assertEquals(board, exceptedBoard);
    }

    @Test
    public void givenNewBoardAndEmptyCellCoord_whenRevealCell_thenCellAndNeighborsRevealed() {

        //Setup
        int row = 0;
        int col = 2;
        Board board = TestUtils.buildInitializedBoard(user);
        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.PLAYING);
        expectedBoard.setRevealedCells(4);
        expectedBoard.getGridCell(0, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(0, 2).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 2).setStatus(CellStatusEnum.VISIBLE);
        // Expected board grid should be look like:
        // |* 1 0|  |# 1 0|
        // |1 2 1|  |# 2 1|
        // |0 1 *|  |# # #|

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        assertEquals(expectedBoard, board);
    }

    @Test
    public void givenNewBoardAndCellCoordWithNeighbors_whenRevealCell_thenOnlyCellRevealed() {

        //Setup
        int row = 2;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.PLAYING);
        expectedBoard.setRevealedCells(1);
        expectedBoard.getGridCell(2, 1).setStatus(CellStatusEnum.VISIBLE);
        // Expected board grid should be look like:
        // |* 1 0|  |# # #|
        // |1 2 1|  |# # #|
        // |0 1 *|  |# 1 #|

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        assertEquals(expectedBoard, board);
    }

    @Test
    public void givenInPlayBoardAndRevealedCellCoord_whenRevealCell_thenDoesNothing() {

        //Setup
        int row = 2;
        int col = 1;
        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.PLAYING);
        expectedBoard.setRevealedCells(1);
        expectedBoard.getGridCell(2, 1).setStatus(CellStatusEnum.VISIBLE);
        // Expected board grid should be look like:
        // |* 1 0|  |# # #|
        // |1 2 1|  |# # #|
        // |0 1 *|  |# 1 #|
        Board board = expectedBoard.clone();

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        expectedBoard.setFinishDatetime(board.getFinishDatetime());
        assertEquals(expectedBoard, board);
    }

    @Test
    public void givenInPlayBoardAndFlaggedCellCoord_whenRevealCell_thenDoesNothing() {

        //Setup
        int row = 2;
        int col = 2;
        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.PLAYING);
        expectedBoard.setRevealedCells(4);
        expectedBoard.getGridCell(0, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(0, 2).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 2).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(2, 2).setStatus(CellStatusEnum.FLAGGED);
        // Expected board grid should be look like:
        // |* 1 0|  |# 1 0|
        // |1 2 1|  |# 2 1|
        // |0 1 *|  |# # F|
        Board board = expectedBoard.clone();

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        assertEquals(expectedBoard, board);
    }

    @Test
    public void givenNewBoardAndMinedCellCoord_whenRevealCell_thenGameLost() {

        //Setup
        int row = 0;
        int col = 0;
        Board board = TestUtils.buildInitializedBoard(user);
        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.LOST);
        expectedBoard.setRevealedCells(1);
        expectedBoard.getGridCell(0, 0).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(2, 2).setStatus(CellStatusEnum.VISIBLE);
        // Expected board grid should be look like:
        // |* 1 0|  |* # #|
        // |1 2 1|  |# # #|
        // |0 1 *|  |# # *|

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        expectedBoard.setFinishDatetime(board.getFinishDatetime());
        assertEquals(expectedBoard, board);
    }

    @Test
    public void givenInPlayBoardAndLastedCellCoord_whenRevealCell_thenWon() {

        //Setup
        int row = 2;
        int col = 0;

        Board board = TestUtils.buildInitializedBoard(user);
        board.setStatus(BoardStatusEnum.PLAYING);
        board.setRevealedCells(6);
        board.getGridCell(0, 0).setStatus(CellStatusEnum.FLAGGED);
        board.getGridCell(0, 1).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(0, 2).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(1, 0).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(1, 2).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(2, 1).setStatus(CellStatusEnum.VISIBLE);
        // Initial board grid should be look like:
        // |* 1 0|  |F 1 0|
        // |1 2 1|  |1 2 1|
        // |0 1 *|  |# 1 #|

        Board expectedBoard = TestUtils.buildInitializedBoard(user);
        expectedBoard.setStatus(BoardStatusEnum.WON);
        expectedBoard.setRevealedCells(7);
        expectedBoard.getGridCell(0, 0).setStatus(CellStatusEnum.FLAGGED);
        expectedBoard.getGridCell(0, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(0, 2).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 0).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(1, 2).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(2, 0).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(2, 1).setStatus(CellStatusEnum.VISIBLE);
        expectedBoard.getGridCell(2, 2).setStatus(CellStatusEnum.FLAGGED);
        // Expected board grid should be look like:
        // |* 1 0|  |F 1 0|
        // |1 2 1|  |1 2 1|
        // |0 1 *|  |0 1 F|

        //Execute
        gameEngineService.revealCell(board, row, col);

        //Verify
        expectedBoard.setStartedDatetime(board.getStartedDatetime());
        expectedBoard.setFinishDatetime(board.getFinishDatetime());
        assertEquals(expectedBoard, board);
    }

}
