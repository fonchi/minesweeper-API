package com.deviget.minesweeperAPI;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.domain.Cell;
import com.deviget.minesweeperAPI.domain.User;
import com.deviget.minesweeperAPI.enumeration.BoardStatusEnum;
import com.deviget.minesweeperAPI.enumeration.CellStatusEnum;
import com.deviget.minesweeperAPI.util.TimeUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TestUtils {

    public static final String SUCCESS_BOARD_REQUEST = "/json/success_board_request.json";
    public static final String SUCCESS_BOARD_RESPONSE = "/json/success_board_response.json";
    public static final String SUCCESS_REVEAL_REQUEST = "/json/success_reveal_request.json";
    public static final String SUCCESS_REVEAL_RESPONSE = "/json/success_reveal_response.json";
    public static final String SUCCESS_FLAG_REQUEST = "/json/success_flag_request.json";
    public static final String SUCCESS_FLAG_RESPONSE = "/json/success_flag_response.json";

    public static User buildUser() {

        return User.builder()
                .id("asd123")
                .username("fonchi")
                .email("alfonsovallone@gmail.com")
                .creationDatetime(Instant.now())
                .build();
    }

    public static Board buildInitializedBoard(User user) {

        // Board grid shape:
        // |* 1 0|
        // |1 2 1|
        // |0 1 *|
        LinkedHashMap<String, Cell> grid = new LinkedHashMap<>();
        grid.put("(0,0)", new Cell(0, 0, CellStatusEnum.HIDDEN, true, 0));
        grid.put("(0,1)", new Cell(0, 1, CellStatusEnum.HIDDEN, false, 1));
        grid.put("(0,2)", new Cell(0, 2, CellStatusEnum.HIDDEN, false, 0));
        grid.put("(1,0)", new Cell(1, 0, CellStatusEnum.HIDDEN, false, 1));
        grid.put("(1,1)", new Cell(1, 1, CellStatusEnum.HIDDEN, false, 2));
        grid.put("(1,2)", new Cell(1, 2, CellStatusEnum.HIDDEN, false, 1));
        grid.put("(2,0)", new Cell(2, 0, CellStatusEnum.HIDDEN, false, 0));
        grid.put("(2,1)", new Cell(2, 1, CellStatusEnum.HIDDEN, false, 1));
        grid.put("(2,2)", new Cell(2, 2, CellStatusEnum.HIDDEN, true, 0));

        return Board.builder()
                .id("8e2190a09d4545969576524f82bce29c")
                .user(user)
                .rowSize(3)
                .colSize(3)
                .minesAmount(2)
                .status(BoardStatusEnum.NEW)
                .creationDatetime(getSpecificTime())
                .grid(grid)
                .build();
    }

    public static Board buildEmptyBoard(User user) {

        Board board = Board.buildBoard(user, 3, 3, 2);
        board.setId("8e2190a09d4545969576524f82bce29c");
        board.setCreationDatetime(getSpecificTime());
        return board;
    }

    private static Instant getSpecificTime() {
        return TimeUtils.getInstant(2021, 02, 19, 00, 00, 00, 00);
    }

    public static String getNewBoardRequest() throws Exception {
        return loadFile(SUCCESS_BOARD_REQUEST);
    }

    public static String getNewBoardResponse() throws Exception {
        return loadFile(SUCCESS_BOARD_RESPONSE);
    }

    public static String getRevealRequest() throws Exception {
        return loadFile(SUCCESS_REVEAL_REQUEST);
    }

    public static String getRevealResponse() throws Exception {
        return loadFile(SUCCESS_REVEAL_RESPONSE);
    }

    public static String getFlagRequest() throws Exception {
        return loadFile(SUCCESS_FLAG_REQUEST);
    }

    public static String getFlagResponse() throws Exception {
        return loadFile(SUCCESS_FLAG_RESPONSE);
    }

    /**
     * Loads a resource text file
     *
     * @param
     * @return
     * @throws
     */
    public static String loadFile(String file) throws IOException {
        return IOUtils.toString(TestUtils.class.getResourceAsStream(file), UTF_8);
    }
}
