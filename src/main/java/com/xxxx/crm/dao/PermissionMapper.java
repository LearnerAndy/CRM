package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {
    /**
     * 角色所拥有权限数量
     * @param roleId
     * @return
     */
    Integer countPermissionByRoleId(Integer roleId);

    /**
     * 删除角色所有权限
     * @param roleId
     * @return
     */
    Integer deletePermissionsByRoleId(Integer roleId);

    /**
     * 根据角色id 查询角色拥有的菜单id
     * @param roleId
     * @return
     */
    List<Integer> queryRoleHasModuleIdByRoleId(Integer roleId);

    List<String> queryUserHasRolePermission(Integer userId);

    int countPermissionByModuleId(Integer mid);


    int deletePermissionsByModuleId(Integer mid);
}