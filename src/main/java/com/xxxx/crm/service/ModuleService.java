package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dto.TreeDto;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 资源数据查询并回显
     *
     * @return
     */
    public List<TreeDto> queryAllModules(Integer roleId) {
        List<TreeDto> treeDtos = moduleMapper.queryAllModules();
        List<Integer> roleHasMids = permissionMapper.queryRoleHasModuleIdByRoleId(roleId);
        System.out.println(roleId);
        System.out.println(roleHasMids);
        if (null != roleHasMids && roleHasMids.size() > 0) {
            treeDtos.forEach(treeDto -> {
                if (roleHasMids.contains(treeDto.getId())) {
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }

    /**
     * 菜单资源展示查询（所有模块）
     */
    public Map<String, Object> moduleList() {
        Map<String, Object> result = new HashMap<>();
        List<Module> modules = moduleMapper.queryModules();
        result.put("count", modules.size());
        result.put("data", modules);
        result.put("code", 0);
        result.put("msg", "");
        return result;

    }
//    @Transactional(propagation = Propagation.REQUIRED)
//    public void saveModule(Module module) {
//        AssertUtil.isTrue(insertSelective(module)<1,"菜单添加失败!");
//    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module) {
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "请输入菜单名！");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不正确！");
        AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName()), "该层级下已存在同名菜单！");
        if (grade == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "请指定二级菜单url！");
            AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndUrl(module.getGrade(), module.getUrl()), "二级菜单url不可重复！");
        }
        if (grade != 0) {
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null == parentId || null == moduleMapper.selectByPrimaryKey(parentId), "请指定上级菜单！");
        }
        String optValue = module.getOptValue();
        AssertUtil.isTrue(StringUtils.isBlank(optValue), "请输入权限码！");
        AssertUtil.isTrue(null != moduleMapper.queryModuleByOptValue(optValue), "权限码重复！");
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(module) < 1, "菜单添加失败!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        AssertUtil.isTrue(null == module.getId() || null == moduleMapper.selectByPrimaryKey(module.getId()), "待更新记录不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "请输入模块名！");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不正确！");
        Module temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        if (null != temp) {
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())), "该层级下菜单已存在！");
        }
        if (grade == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "请输入url！");
            temp = moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            if (null != temp) {
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())), "该层级下url已存在！");
            }
        }
        if (grade != 0) {
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null == parentId || null == selectByPrimaryKey(parentId), "请指定上级菜单！");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不可为空！");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (null != temp) {
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())), "权限码重复！");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) < 1, "菜单更新失败!");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid) {
        Module temp = selectByPrimaryKey(mid);
        AssertUtil.isTrue(null == mid || null == temp,"待删除记录不存在！");
        int count = moduleMapper.countSubModuleByParenId(mid);
        AssertUtil.isTrue(count>0,"请先删除子菜单！");
        count = permissionMapper.countPermissionByModuleId(mid);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid)<count,"菜单删除失败！");
        }
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp) < 1, "菜单删除失败！");
    }
}
