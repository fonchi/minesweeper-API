package com.deviget.minesweeperAPI.controller;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.dto.BoardPostRequestDto;
import com.deviget.minesweeperAPI.dto.BoardPostResponseDto;
import com.deviget.minesweeperAPI.dto.BoardRevealPostRequestDto;
import com.deviget.minesweeperAPI.dto.BoardRevealPostResponseDto;
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
@RequestMapping("minesweeper")
public class BoardController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardService boardService;

    @RequestMapping(value = "/users/{user_id}/boards", method = RequestMethod.POST)
    public ResponseEntity<?> createBoard(@PathVariable("user_id") @NotBlank String userId, @RequestBody @Valid BoardPostRequestDto dto) {

        logger.info("userId: " + userId);
        logger.info("BoardPostRequestDto: " + dto.toString());

        Board board = boardService.createBoard(userId, dto.getRowSize(), dto.getColSize(), dto.getMinesAmount());

        logger.info("Created Board: " + board.toString());

        if (isNull(board)) {
            logger.info("Board is " + board);
            return ResponseEntity.status(500).body("Error to create board");
        }

        BoardPostResponseDto responseDto = BoardPostResponseDto.fromEntity(board);
        logger.info("BoardPostResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{user_id}/boards/{board_id}", method = RequestMethod.POST)
    public ResponseEntity<?> createBoard(@PathVariable("user_id") @NotBlank String userId,
                                         @PathVariable("board_id") @NotBlank String boardId,
                                         @RequestBody @Valid BoardRevealPostRequestDto dto) {

        logger.info("userId: " + userId);
        logger.info("boardId: " + userId);
        logger.info("BoardRevealPostRequestDto: " + dto.toString());

        Board board = boardService.revealCell(userId, boardId, dto.getSelectedRowNum(), dto.getSelectedColNum());

        logger.info("Board revealed: " + board.toString());

        if (isNull(board)) {
            logger.info("Board is " + board);
            return ResponseEntity.status(500).body("Error to reveal board");
        }

        BoardRevealPostResponseDto responseDto = BoardRevealPostResponseDto.fromEntity(board);
        logger.info("BoardRevealPostResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }


}
