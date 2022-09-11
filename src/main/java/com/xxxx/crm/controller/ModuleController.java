package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dto.TreeDto;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RequestMapping("module")
@Controller
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    /**
     * 资源数据查询
     *
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeDto> queryAllModules(Integer roleId) {
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 菜单资源展示查询（所有模块）
     *
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> moduleList() {
        return moduleService.moduleList();
    }
    @RequestMapping("index")
    public String index() {
        return "module/module";
    }

    /**
     * 添加资源页视图转发
     */
    @RequestMapping("addModulePage")
    public String addModulePage(Integer grade,Integer parentId, Model model){
        model.addAttribute("grade",grade);
        model.addAttribute("parentId",parentId);
        return "module/add";
    }

    /**
     * 菜单添加
     * @param module
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveModule(Module module){
        moduleService.saveModule(module);
        return success("菜单添加成功！");
    }

    /**
     * 更新资源页视图转发
     * @return
     */
    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id,Model model){
        model.addAttribute("module",moduleService.selectByPrimaryKey(id));
        return "module/update";
    }

    /**
     * 菜单更新
     * @param module
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("菜单更新成功！");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModuleById(id);
        return success("菜单删除成功");
    }
}
