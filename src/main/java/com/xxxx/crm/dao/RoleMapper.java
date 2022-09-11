package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    /*查询角色列表*/
    @MapKey("r.id")
    public List<Map<String,Object>> queryAllRoles(Integer id);

    Role queryRoleByRoleName(String roleName);
}