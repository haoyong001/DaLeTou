package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ author Mr. Hao
 * @ date 2022-11-23   18:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WinDataInfo implements Serializable {

    private int id;

    private String redAndBlue;

    private String historyData;

    private int theFirstPrizeCount;

    private int secondAwardCount;

    private int moneyCount;

    private String dateTime;
}
