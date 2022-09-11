package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 查询所有的销售人员
     *
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    public UserModel userLogin(String userName, String userPwd) {
        //1. 验证参数
        checkLoginParams(userName, userPwd);
        //2. 根据用户名，查询用户对象
        User user = userMapper.queryUserByUserName(userName);
        //3. 判断用户是否存在
        AssertUtil.isTrue(null == user, "用户不存在或已注销！");

        //用户对象不为空,校验密码
        checkLoginPwd(userPwd, user.getUserPwd());
        return bulidUserInfo(user);

    }

    private UserModel bulidUserInfo(User user) {
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    private void checkLoginPwd(String userPwd, String upwd) {
        userPwd = Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(upwd), "用户密码不正确！");
    }


    /**
     * 验证用户登录参数
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        // 判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");
        // 判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "密码不能为空！");
    }

    /**
     * @param userId          用户ID
     * @param oldPassword     原始密码
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        // 通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 1. 参数校验
        checkPasswordParams(user, oldPassword, newPassword, confirmPassword);
        // 2. 设置新密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户密码更新失败");
    }

    private void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPassword) {
        // user对象 非空验证
        AssertUtil.isTrue(null == user, "用户未登录或不存在！");
        // 原始密码 非空验证
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "请输入原始密码！");
        // 原始密码要与数据库中的密文密码保持一致
        AssertUtil.isTrue(!Md5Util.encode(oldPassword).equals(user.getUserPwd()), "原始密码不正确！");
        // 新密码 非空校验
        AssertUtil.isTrue(null == newPassword, "请输入新密码！");
        // 新密码与原始密码不能相同
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能与原始密码相同！");
        // 确认密码与新密码一致
        AssertUtil.isTrue(!confirmPassword.equals(newPassword), "确认密码与新密码不一致");
    }

    /**
     * 多条件分页查询用户数据
     *
     * @param query
     * @return
     */
    public Map<String, Object> queryUserByParams(UserQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(query));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加用户
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        checkParams(user.getUserName(), user.getEmail(), user.getPhone());
        AssertUtil.isTrue(null != userMapper.queryUserByUserName(user.getUserName()), "该用户已存在！");
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
//        AssertUtil.isTrue(userMapper.insertSelective(user) == null, "用户添加失败！");
        AssertUtil.isTrue(userMapper.insertHasKey(user) == null, "用户添加失败！");
        relationUserRole(user.getId(), user.getRoleIds());

    }

    /**
     * 关系用户角色
     * 用户角色分配
     *
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //如果用户原始角色存在 首先清空原始所有角色 添加新的角色记录到用户角色表
        int count = userRoleMapper.countUserRoleByUserId(userId);
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败!");
        }
        if (StringUtils.isNotBlank(roleIds)) {
            List<UserRole> userRoles = new ArrayList<>();
            for (String s : roleIds.split(",")) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.parseInt(s));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoles.add(userRole);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoles) < userRoles.size(),"用户角色分配失败!");
        }
    }

    /**
     * 参数校验
     *
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(email), "请输入邮箱地址！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号码格式不正确！");
    }

    /**
     * 更新用户
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null == temp, "待更新记录不存在！");
        checkParams(user.getUserName(), user.getEmail(), user.getPhone());
        temp.setUpdateDate(new Date());
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户更新失败！");
        Integer userId = userMapper.queryUserByUserName(user.getUserName()).getId();
        System.out.println(userId);
        System.out.println(user.getId());
        relationUserRole(userId, user.getRoleIds());
    }

    /**
     * 删除用户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null == ids || ids.length == 0, "请选择待删除的用户记录！");
        AssertUtil.isTrue(deleteBatch(ids) != ids.length, "用户记录删除失败！");
    }


}
