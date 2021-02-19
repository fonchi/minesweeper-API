package com.deviget.minesweeperAPI.controller;

import com.deviget.minesweeperAPI.domain.Board;
import com.deviget.minesweeperAPI.dto.BoardRequestDto;
import com.deviget.minesweeperAPI.dto.BoardResponseDto;
import com.deviget.minesweeperAPI.dto.RevealFlagRequestDto;
import com.deviget.minesweeperAPI.dto.RevealFlagResponseDto;
import com.deviget.minesweeperAPI.error.InternalServerException;
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

    @RequestMapping(value = "/users/{username}/boards", method = RequestMethod.POST)
    public ResponseEntity<?> createBoard(@PathVariable("username") @NotBlank String username,
                                         @RequestBody @Valid BoardRequestDto dto) {

        logger.info("username: " + username);
        logger.info("BoardRequestDto: " + dto.toString());

        Board board = boardService.createBoard(username, dto.getRowSize(), dto.getColSize(), dto.getMinesAmount());
        if (isNull(board))
            throw new InternalServerException("Error to create board");
        logger.info("Board created: " + board.toString());
        board.drawGrid();

        BoardResponseDto responseDto = BoardResponseDto.fromEntity(board);
        logger.info("BoardResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{username}/boards/{board_id}/reveals", method = RequestMethod.POST)
    public ResponseEntity<?> revealCell(@PathVariable("username") @NotBlank String username,
                                        @PathVariable("board_id") @NotBlank String boardId,
                                        @RequestBody @Valid RevealFlagRequestDto dto) {

        logger.info("username: " + username);
        logger.info("boardId: " + boardId);
        logger.info("RevealFlagRequestDto: " + dto.toString());

        Board board = boardService.revealCell(username, boardId, dto.getSelectedRowNum(), dto.getSelectedColNum());
        if (isNull(board))
            throw new InternalServerException("Error to reveal board cell");
        logger.info("Board revealed: " + board.toString());
        board.drawGrid();

        RevealFlagResponseDto responseDto = RevealFlagResponseDto.fromEntity(board);
        logger.info("RevealFlagResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{username}/boards/{board_id}/flags", method = RequestMethod.POST)
    public ResponseEntity<?> flagCell(@PathVariable("username") @NotBlank String username,
                                      @PathVariable("board_id") @NotBlank String boardId,
                                      @RequestBody @Valid RevealFlagRequestDto dto) {

        logger.info("username: " + username);
        logger.info("boardId: " + boardId);
        logger.info("RevealFlagRequestDto: " + dto.toString());

        Board board = boardService.flagCell(username, boardId, dto.getSelectedRowNum(), dto.getSelectedColNum());
        if (isNull(board))
            throw new InternalServerException("Error to flag board cell");
        logger.info("Board flagged: " + board.toString());
        board.drawGrid();

        RevealFlagResponseDto responseDto = RevealFlagResponseDto.fromEntity(board);
        logger.info("RevealFlagResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

    @RequestMapping(value = "/users/{username}/boards/{board_id}", method = RequestMethod.GET)
    public ResponseEntity<?> getBoard(@PathVariable("username") @NotBlank String username,
                                      @PathVariable("board_id") @NotBlank String boardId) {

        logger.info("username: " + username);
        logger.info("boardId: " + boardId);

        Board board = boardService.getBoardByIdAndUsername(boardId, username);
        logger.info("Board: " + board.toString());
        board.drawGrid();

        BoardResponseDto responseDto = BoardResponseDto.fromEntity(board);
        logger.info("BoardResponseDto: " + responseDto);
        return ResponseEntity.ok(responseDto);
    }

}
