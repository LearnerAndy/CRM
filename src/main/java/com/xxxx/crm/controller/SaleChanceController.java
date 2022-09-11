package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private UserService userService;

    /**
     * 多条件分页查询营销机会
     *
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    @RequirePermission(code = "101001")
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery query,
                                                       Integer flag,
                                                       HttpServletRequest request) {

        if (null != flag && flag == 1) {
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            query.setAssignMan(userId);
        }
        System.out.println(query);
        return saleChanceService.querySaleChanceByParams(query);
    }

    /**
     * 进入营销机会页面
     *
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    /**
     * 添加
     *
     * @param request
     * @param saleChance
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance) {

        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        saleChance.setCreateMan(trueName);
        saleChanceService.saveSaleChance(saleChance);

        return success("营销机会数据添加成功！");

    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(HttpServletRequest request, SaleChance saleChance) {

        /*Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        saleChance.setCreateMan(trueName);*/
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功！");

    }

    /**
     * 页面转发
     *
     * @param id
     * @param
     * @return
     */
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, HttpServletRequest request) {
        //如果是修改操作那么需要将修改的数据映射在页面中
        if (id != null) {
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            AssertUtil.isTrue(saleChance == null, "数据异常，请重试");
            request.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    @RequestMapping("user/queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

    /**
     * 营销机会数据删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids) {
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功！");
    }


    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id, devResult);
        return success("开发状态更新成功");
    }
}
