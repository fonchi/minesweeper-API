package com.deviget.minesweeperAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@ToString
public class BoardRequestDto {

    @Min(value = 2, message = "row_size should not be less than 2")
    @JsonProperty("row_size")
    private int rowSize;
    @Min(value = 2, message = "col_size should not be less than 2")
    @JsonProperty("col_size")
    private int colSize;
    @Min(value = 1, message = "mines_amount should not be less than 1")
    @JsonProperty("mines_amount")
    private int minesAmount;
}
