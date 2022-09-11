package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    /**
     * 登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();

        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);

        return resultInfo;
    }

    /**
     * 修改密码
     *
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @PostMapping("updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword) {
        ResultInfo resultInfo = new ResultInfo();

        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);

        return resultInfo;
    }

    /**
     * 跳转到修改密码界面
     *
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }

    /**
     * 多条件查询用户数据
     *
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryUserByParams(userQuery);
    }

    /**
     * 进入用户页面
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    /**
     * 添加用户
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("用户添加成功！");
    }

    /**
     * 更新用户
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("用户更新成功！");
    }

    /**
     * 进入用户添加或更新页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id, Model model) {
        if (null != id) {
            model.addAttribute("user", userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }

    /**
     * 删除用户
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {
        userService.deleteUserByIds(ids);
        return success("用户记录删除成功！");
    }
}
