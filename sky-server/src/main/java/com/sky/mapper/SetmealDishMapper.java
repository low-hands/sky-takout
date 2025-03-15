package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    // 根据dish id查询套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
