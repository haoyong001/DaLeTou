package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ author Mr. Hao
 * @ date 2023-10-11   20:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDaLeTouEntity {

    private int lotteryDrawNum;

    private String lotteryDrawResult;

    private String lotteryDrawTime;

}
