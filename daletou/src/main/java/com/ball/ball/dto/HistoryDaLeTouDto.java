package com.ball.ball.dto;

import com.ball.ball.entity.HistoryDaLeTouEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2023-10-11   19:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDaLeTouDto {
    //"lotteryDrawNum":"23115","lotteryDrawResult":"07 10 23 31 34 02 10","lotteryDrawTime":"2023-10-09",

    private List<HistoryDaLeTouEntity> list;

}
