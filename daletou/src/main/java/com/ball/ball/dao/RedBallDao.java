package com.ball.ball.dao;

import com.ball.ball.entity.RedBallInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   17:27
 */
@Mapper
public interface RedBallDao {

    void insertOne(RedBallInfo r);

    void insertBatch(List<RedBallInfo> redBallInfoList);

    RedBallInfo selectById(int id);

    RedBallInfo selectByRedBall(String redBall);

    List<RedBallInfo> queryByStartAndEndIdx(int arg0, int arg1);
}
