package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role, Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询角色列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer id) {
        return roleMapper.queryAllRoles(id);
    }

    /**
     * 添加角色
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role) {
        /**
         1.参数效验
         角色名非空
         不可重复
         2.设置默认参数
         is_valid
         c  d
         u  d
         3.执行添加，判断结果
         */
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "请输入角色名！");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null != temp, "该角色已存在！");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(role) < 1, "角色记录添加失败!");
    }

    /**
     * 更新角色
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        /**
         1.参数效验
         id 非空 修改数据非空
         角色名 非空
         不可重复
         2.设置默认参数
         u d
         3.执行添加，判断结果
         */
        AssertUtil.isTrue(null == role.getId() || null == selectByPrimaryKey(role.getId()), "待修改记录不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "请输入角色名！");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(role.getId())), "该角色已存在！");
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(role) < 1, "角色记录更新失败！");
    }

    /**
     * 删除角色
     *
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId) {
        //id非空，记录非空
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null == roleId || null == temp, "该角色记录不存在！");
        temp.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp) < 1, "角色记录删除失败！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mids, Integer roleId) {
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null == roleId || null == temp, "待授权角色不存在！");
        int count = permissionMapper.countPermissionByRoleId(roleId);
        System.out.println(permissionMapper.countPermissionByRoleId(roleId));
        if (count > 0) {
            AssertUtil.isTrue(permissionMapper.deletePermissionsByRoleId(roleId) < count, "权限分配失败！");
        }
        if (null != mids && mids.length > 0) {
            List<Permission> permissions = new ArrayList<>();
            for (Integer mid : mids) {
                Permission permission = new Permission();
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissions.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }
}
