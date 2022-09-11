package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    int countUserRoleByUserId(Integer userId);

    int deleteUserRoleByUserId(Integer userId);
}