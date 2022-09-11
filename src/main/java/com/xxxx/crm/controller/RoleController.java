package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RequestMapping("role")
@Controller
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    /**
     * 查询角色列表
     *
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String, Object>> queryAllRoles(Integer id) {
        return roleService.queryAllRoles(id);
    }

    /**
     * 添加角色关注主页面视图转发
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "role/role";
    }

    /**
     * 列表查询 方法
     *
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> userList(RoleQuery roleQuery) {
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 添加或更新页面转发
     */
    @RequestMapping("addOrUpdateRolePage")
    public String addUserPage(Integer id, Model model) {

        if (null != id) {
            model.addAttribute("role", roleService.selectByPrimaryKey(id));
        }
        return "role/add_update";
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role) {
        roleService.saveRole(role);
        return success("角色记录添加成功!");
    }

    /**
     * 更新角色
     * @param role
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role) {
        roleService.updateRole(role);
        return success("角色记录更新成功!");
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色记录删除成功！");
    }

    /**
     * 角色授权视图转发
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }
    /**
     * 权限记录添加
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids,roleId);
        return success("权限添加成功!");
    }
}
