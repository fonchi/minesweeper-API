package com.deviget.minesweeperAPI.controller;

import com.deviget.minesweeperAPI.TestUtils;
import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BoardController.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Test
    public void givenValidConfig_whenPostCreateBoard_thenSuccess() throws Exception {

        //Setup
        String requestJson = TestUtils.getNewBoardRequest();
        String responseJson = TestUtils.getNewBoardResponse();

        User user = TestUtils.buildUser();
        Board board = TestUtils.buildInitializedBoard(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/fonchi/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        //Mock
        when(boardService.createBoard(user.getUsername(), board.getRowSize(), board.getColSize(),
                board.getMinesAmount())).thenReturn(board);

        //Execute and Verify
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void givenValidData_whenPostRevealCell_thenSuccess() throws Exception {

        //Setup
        String requestJson = TestUtils.getRevealRequest();
        String responseJson = TestUtils.getRevealResponse();

        int selectedRow = 1;
        int selectedCol = 1;
        User user = TestUtils.buildUser();
        Board board = TestUtils.buildInitializedBoard(user);
        board.setStatus(BoardStatusEnum.PLAYING);
        board.setStartedDatetime(board.getCreationDatetime().plusSeconds(30));
        board.setFinishDatetime(board.getCreationDatetime().plusSeconds(120));
        board.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/fonchi/boards/8e2190a09d4545969576524f82bce29c/reveals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        //Mock
        when(boardService.revealCell(user.getUsername(), board.getId(), selectedRow, selectedCol)).thenReturn(board);

        //Execute and Verify
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void givenValidData_whenPostFlagCell_thenSuccess() throws Exception {

        //Setup
        String requestJson = TestUtils.getFlagRequest();
        String responseJson = TestUtils.getFlagResponse();

        int selectedRow = 2;
        int selectedCol = 2;
        User user = TestUtils.buildUser();
        Board board = TestUtils.buildInitializedBoard(user);
        board.setStatus(BoardStatusEnum.PLAYING);
        board.setStartedDatetime(board.getCreationDatetime().plusSeconds(30));
        board.setFinishDatetime(board.getCreationDatetime().plusSeconds(120));
        board.getGridCell(1, 1).setStatus(CellStatusEnum.VISIBLE);
        board.getGridCell(2, 2).setStatus(CellStatusEnum.FLAGGED);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/fonchi/boards/8e2190a09d4545969576524f82bce29c/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        //Mock
        when(boardService.flagCell(user.getUsername(), board.getId(), selectedRow, selectedCol)).thenReturn(board);

        //Execute and Verify
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }
}
