package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ author Mr. Hao
 * @ date 2022-11-26   16:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoWinDataInfo implements Serializable {
    private int id;

    private String noWinNum;

    private int moneyCount;

}
