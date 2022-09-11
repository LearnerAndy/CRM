package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;

import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;


    /**
     * 客户开发主页面
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "cusDevPlan/cus_dev_plan";
    }


    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Model model, Integer sid) {
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sid);
        model.addAttribute("saleChance", saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 查询
     *
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery query) {
        return cusDevPlanService.queryCusDevPlanByParams(query);
    }

    /**
     * 添加
     *
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.saveCusDevPlan(cusDevPlan);
        return success("计划项添加成功");
    }

    /**
     * 更新
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项修改成功");
    }

    /**
     * 跳转到计划数据项页面
     *
     * @return
     */
    @RequestMapping("addOrUpdateCusDevPlanPage")
    public String addOrUpdateCusDevPlanPage(Integer sid, Integer id, Model model) {
        model.addAttribute("cusDevPlan", cusDevPlanService.selectByPrimaryKey(id));
        model.addAttribute("sid", sid);
        return "cusDevPlan/add_update";
    }

    /**
     * 删除计划
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划删除成功");
    }


}
