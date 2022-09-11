package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.dto.TreeDto;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    /**
     * 资源数据查询
     * @return
     */
    List<TreeDto> queryAllModules();

    /**
     *  菜单资源展示查询（所有模块）
     * @return
     */
    List<Module> queryModules();

    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);

    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade, @Param("url") String url);

    Module queryModuleByOptValue(String optValue);


    int countSubModuleByParenId(Integer mid);
}