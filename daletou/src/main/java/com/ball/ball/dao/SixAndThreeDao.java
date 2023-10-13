package com.ball.ball.dao;

import com.ball.ball.entity.SixAndThreeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2023-10-13   3:59
 */
@Mapper
public interface SixAndThreeDao {

    void batchSave(List<SixAndThreeEntity> list);

    List<SixAndThreeEntity> findAll(int startIdx,int endIdx);
}
