package com.deviget.minesweeperAPI.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
@ToString
public class RevealFlagPostRequestDto {

    @Min(value = 0, message = "selected_row_num should not be less than 0")
    @JsonProperty("selected_row_num")
    private int selectedRowNum;
    @Min(value = 0, message = "selected_col_num should not be less than 0")
    @JsonProperty("selected_col_num")
    private int selectedColNum;
}
