package com.fitsoft.shop.repository;

import com.fitsoft.shop.bean.User;
import org.apache.ibatis.annotations.Select;

/**
 * UserMapper 数据访问类
 */
public interface UserMapper {

    @Select("select * from ec_user where login_name = #{ddd}")
    User login(String loginName);
}