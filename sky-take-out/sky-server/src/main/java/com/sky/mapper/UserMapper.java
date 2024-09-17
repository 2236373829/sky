package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author xyzZero3
 * @date 2024/9/11 18:05
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 注册新用户
     *
     * @param user
     */
    void registerUser(User user);

    @Select("select id, openid, name, phone, sex, id_number, avatar, create_time from user where id = #{id}")
    User getById(Long id);

    Integer countByMap(Map<String, Object> map);
}
