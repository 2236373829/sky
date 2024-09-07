package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xyzZero3
 * @date 2024/9/7 11:20
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     * @return
     */
    int batchInsert(List<DishFlavor> flavors);
}
