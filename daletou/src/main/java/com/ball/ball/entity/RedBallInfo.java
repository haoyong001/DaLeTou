package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   16:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedBallInfo implements Serializable {
    private int id;

    private String redBall;

    private String createTime;

    public RedBallInfo(String s, String s1) {
        this.redBall = s;
        this.createTime = s1;
    }
}
