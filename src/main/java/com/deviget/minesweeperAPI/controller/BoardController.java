package com.deviget.minesweeperAPI.controller;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.dto.BoardPostRequestDto;
import com.deviget.minesweeperAPI.dto.BoardPostResponseDto;
import com.deviget.minesweeperAPI.dto.RevealFlagPostRequestDto;
import com.deviget.minesweeperAPI.dto.RevealFlagPostResponseDto;
import com.deviget.minesweeperAPI.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static java.util.Objects.isNull;

@RestController
public class BoardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;

    @RequestMapping(value = "/users/{user_id}/boards", method = RequestMethod.POST)
    public ResponseEntity<?> createBoard(@PathVariable("user_id") @NotBlank String userId,
                                         @RequestBody @Valid BoardPostRequestDto dto) {

        logger.info("userId: " + userId);
        logger.info("BoardPostRequestDto: " + dto.toString());

        Board board = boardService.createBoard(userId, dto.getRowSize(), dto.getColSize(), dto.getMinesAmount());
        if (isNull(board)) {
            logger.info("Board is " + board);
            return ResponseEntity.status(500).body("Error to create board");
        }
        logger.info("Board created: " + board.toString());
        board.drawGrid();

        BoardPostResponseDto responseDto = BoardPostResponseDto.fromEntity(board);
        logger.info("BoardPostResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{user_id}/boards/{board_id}/reveals", method = RequestMethod.POST)
    public ResponseEntity<?> revealCell(@PathVariable("user_id") @NotBlank String userId,
                                        @PathVariable("board_id") @NotBlank String boardId,
                                        @RequestBody @Valid RevealFlagPostRequestDto dto) {

        logger.info("userId: " + userId);
        logger.info("boardId: " + userId);
        logger.info("RevealFlagPostRequestDto: " + dto.toString());

        Board board = boardService.revealCell(userId, boardId, dto.getSelectedRowNum(), dto.getSelectedColNum());
        if (isNull(board)) {
            logger.info("Board is " + board);
            return ResponseEntity.status(500).body("Error to reveal board cell");
        }
        logger.info("Board revealed: " + board.toString());
        board.drawGrid();

        RevealFlagPostResponseDto responseDto = RevealFlagPostResponseDto.fromEntity(board);
        logger.info("RevealFlagPostResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{user_id}/boards/{board_id}/flags", method = RequestMethod.POST)
    public ResponseEntity<?> flagCell(@PathVariable("user_id") @NotBlank String userId,
                                      @PathVariable("board_id") @NotBlank String boardId,
                                      @RequestBody @Valid RevealFlagPostRequestDto dto) {

        logger.info("userId: " + userId);
        logger.info("boardId: " + userId);
        logger.info("RevealFlagPostRequestDto: " + dto.toString());

        Board board = boardService.flagCell(userId, boardId, dto.getSelectedRowNum(), dto.getSelectedColNum());
        if (isNull(board)) {
            logger.info("Board is " + board);
            return ResponseEntity.status(500).body("Error to flag board cell");
        }
        logger.info("Board flagged: " + board.toString());
        board.drawGrid();

        RevealFlagPostResponseDto responseDto = RevealFlagPostResponseDto.fromEntity(board);
        logger.info("RevealFlagPostResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

}
