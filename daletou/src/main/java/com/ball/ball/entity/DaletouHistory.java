package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 大乐透历史开奖
 * @ author Mr. Hao
 * @ date 2022-11-23   17:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DaletouHistory implements Serializable {

    private int issueNo;

    private String redOne;

    private String redTwo;

    private String redThree;

    private String redFour;

    private String redFive;

    private String blueOne;

    private String blueTwo;

    private String dateTime;

}
