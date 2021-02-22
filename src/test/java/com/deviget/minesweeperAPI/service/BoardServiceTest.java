package com.deviget.minesweeperAPI.service;

import com.deviget.minesweeperAPI.TestUtils;
import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.error.BadRequestException;
import com.deviget.minesweeperAPI.error.NotFoundException;
import com.deviget.minesweeperAPI.lock.Lock;
import com.deviget.minesweeperAPI.lock.LockService;
import com.deviget.minesweeperAPI.repository.BoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests to board service properties verification
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private GameEngineService gameEngineServiceMock;
    @MockBean
    private BoardRepository boardRepositoryMock;
    @MockBean
    private LockService lockServiceMock;
    private User user;

    @Before
    public void setUp() {
        user = TestUtils.buildUser();
        when(userServiceMock.getUserByUsername(user.getUsername())).thenReturn(user);

        Board board = TestUtils.buildInitializedBoard(user);
        doAnswer(invocation -> {
            ((Board) invocation.getArgument(0)).setGrid(board.getGrid());
            return null;
        }).when(gameEngineServiceMock).initializeBoardScenario(any(Board.class));

        doAnswer(invocation -> {
            Board toRevealBoard = invocation.getArgument(0);
            int row = invocation.getArgument(1);
            int col = invocation.getArgument(2);
            toRevealBoard.start();
            toRevealBoard.getGridCell(row, col).setStatus(CellStatusEnum.VISIBLE);
            toRevealBoard.incrementRevealedCells();
            return null;
        }).when(gameEngineServiceMock).revealCell(board, 1, 1);

        when(boardRepositoryMock.save(any(Board.class))).thenReturn(board);
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(board);

        when(lockServiceMock.lock(board.getId())).thenReturn(new Lock(board.getId(), 60));
    }

    @Test
    public void givenValidConfigParams_whenCreateBoard_thenBoardSuccessfullyCreated() {

        //Setup
        int rows = 3;
        int cols = 3;
        int mines = 2;
        Board board = TestUtils.buildInitializedBoard(user);

        //Execute
        Board result = boardService.createBoard(user.getUsername(), rows, cols, mines);

        //Verify
        assertEquals(board, result);
    }

    @Test
    public void givenZeroMinesAmount_whenCreateBoard_thenBadRequestException() {

        //Setup
        int rows = 3;
        int cols = 3;
        int mines = 0;

        //Execute and Verify
        Exception e = assertThrows(BadRequestException.class, () -> boardService.createBoard(user.getUsername(), rows, cols, mines));
        assertEquals(String.format("minesAmount should be a positive or less than %s", rows * cols), e.getMessage());
    }

    @Test
    public void givenInvalidMinesAmount_whenCreateBoard_thenBadRequestException() {

        //Setup
        int rows = 3;
        int cols = 3;
        int mines = 10;

        //Execute and Verify
        Exception e = assertThrows(BadRequestException.class, () -> boardService.createBoard(user.getUsername(), rows, cols, mines));
        assertEquals(String.format("minesAmount should be a positive or less than %s", rows * cols), e.getMessage());
    }

    @Test
    public void givenInvalidUsername_whenCreateBoard_thenNotFoundException() {

        //Setup
        String invalidUsername = "invalid-username";
        int rows = 3;
        int cols = 3;
        int mines = 2;

        //Mock
        when(userServiceMock.getUserByUsername(invalidUsername)).thenReturn(null);

        //Execute and Verify
        Exception e = assertThrows(NotFoundException.class, () -> boardService.createBoard(invalidUsername, rows, cols, mines));
        assertEquals(String.format("User '%s' not found", invalidUsername), e.getMessage());
    }

    @Test
    public void givenInvalidRowPosition_whenRevealCell_thenBadRequestException() {

        //Setup
        int row = 3;
        int col = 0;
        Board board = TestUtils.buildInitializedBoard(user);

        //Execute and Verify
        Exception e = assertThrows(BadRequestException.class, () -> boardService.revealCell(user.getUsername(), board.getId(), row, col));
        assertEquals(String.format("rowNumber should be bigger than zero and less than %s", board.getRowSize()), e.getMessage());
    }

    @Test
    public void givenInvalidColPosition_whenRevealCell_thenBadRequestException() {

        //Setup
        int row = 0;
        int col = 3;
        Board board = TestUtils.buildInitializedBoard(user);

        //Execute and Verify
        Exception e = assertThrows(BadRequestException.class, () -> boardService.revealCell(user.getUsername(), board.getId(), row, col));
        assertEquals(String.format("colNumber should be bigger than zero and less than %s", board.getColSize()), e.getMessage());
    }

    @Test
    public void givenInvalidUsername_whenRevealCell_thenNotFoundException() {

        //Setup
        String invalidUsername = "invalid-username";
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);

        //Mock
        when(userServiceMock.getUserByUsername(invalidUsername)).thenReturn(null);

        //Execute and Verify
        Exception e = assertThrows(NotFoundException.class, () -> boardService.revealCell(invalidUsername, board.getId(), row, col));
        assertEquals(String.format("User '%s' not found", invalidUsername), e.getMessage());
    }

    @Test
    public void givenInvalidBoard_whenRevealCell_thenNotFoundException() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        board.setId("invalid-id");

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(null);

        //Execute and Verify
        Exception e = assertThrows(NotFoundException.class, () -> boardService.revealCell(user.getUsername(), board.getId(), row, col));
        assertEquals(String.format("Board '%s' not found", board.getId()), e.getMessage());
    }

    @Test
    public void givenWonBoardId_whenRevealCell_thenIdempotentResponse() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        board.setId("won-board-id");
        board.setStatus(BoardStatusEnum.WON);

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(board);

        //Execute
        Board result = boardService.revealCell(user.getUsername(), board.getId(), row, col);

        //Verify
        assertEquals(board, result);
        verify(gameEngineServiceMock, never()).revealCell(board, row, col);
    }

    @Test
    public void givenLostBoardId_whenRevealCell_thenIdempotentResponse() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        board.setId("lost-board-id");
        board.setStatus(BoardStatusEnum.LOST);

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(board);

        //Execute
        Board result = boardService.revealCell(user.getUsername(), board.getId(), row, col);

        //Verify
        assertEquals(board, result);
        verify(gameEngineServiceMock, never()).revealCell(board, row, col);
    }

    @Test
    public void givenInPlayBoardId_whenRevealCell_thenBoardUpdated() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);

        //Execute
        Board result = boardService.revealCell(user.getUsername(), board.getId(), row, col);

        //Verify
        assertNotEquals(board, result);
        assertTrue(result.getGridCell(row, col).isVisible());
    }

    @Test
    public void givenWonBoardId_whenFlagCell_thenIdempotentResponse() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        board.setId("won-board-id");
        board.setStatus(BoardStatusEnum.WON);

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(board);

        //Execute
        Board result = boardService.flagCell(user.getUsername(), board.getId(), row, col);

        //Verify
        assertEquals(board, result);
        verify(lockServiceMock, never()).lock(board.getId());
        verify(boardRepositoryMock, never()).save(board);
    }

    @Test
    public void givenLostBoardId_whenFlagCell_thenIdempotentResponse() {

        //Setup
        int row = 1;
        int col = 1;
        Board board = TestUtils.buildInitializedBoard(user);
        board.setId("lost-board-id");
        board.setStatus(BoardStatusEnum.LOST);

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(board.getId(), user.getId())).thenReturn(board);

        //Execute
        Board result = boardService.flagCell(user.getUsername(), board.getId(), row, col);

        //Verify
        assertEquals(board, result);
        verify(lockServiceMock, never()).lock(board.getId());
        verify(boardRepositoryMock, never()).save(board);
    }

    @Test
    public void givenInPlayBoardId_whenFlagCell_thenBoardUpdated() {

        //Setup
        int row = 0;
        int col = 0;
        Board flaggedBoard = TestUtils.buildInitializedBoard(user);
        flaggedBoard.getGridCell(row, col).setStatus(CellStatusEnum.FLAGGED);

        //Execute
        Board result = boardService.flagCell(user.getUsername(), flaggedBoard.getId(), row, col);

        //Verify
        assertEquals(flaggedBoard, result);
    }

    @Test
    public void givenInPlayBoardIdAndCoordinatesAlreadyFlagged_whenFlagCell_thenIdempotentResult() {

        //Setup
        int row = 0;
        int col = 0;
        Board flaggedBoard = TestUtils.buildInitializedBoard(user);
        flaggedBoard.setId("already-flagged-board-id");
        flaggedBoard.getGridCell(row, col).setStatus(CellStatusEnum.FLAGGED);

        //Mock
        when(boardRepositoryMock.findByIdAndUserId(flaggedBoard.getId(), user.getId())).thenReturn(flaggedBoard);

        //Execute
        Board result = boardService.flagCell(user.getUsername(), flaggedBoard.getId(), row, col);

        //Verify
        assertEquals(flaggedBoard, result);
        verify(boardRepositoryMock, never()).save(flaggedBoard);
    }
}
