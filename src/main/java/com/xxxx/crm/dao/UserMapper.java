package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User, Integer> {

    User queryUserByUserName(String userName);
    @MapKey("u.id")
    public List<Map<String, Object>> queryAllSales();
}