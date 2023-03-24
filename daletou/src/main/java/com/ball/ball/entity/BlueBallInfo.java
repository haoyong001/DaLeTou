package com.ball.ball.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   16:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlueBallInfo implements Serializable {

    private int id;

    private String blueBall;

    private String createTime;

}
