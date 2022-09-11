package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件查询计划项列表
     *
     * @param cusDevPlanQuery
     * @return
     */
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {
        Map<String, Object> map = new HashMap<String, Object>();
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        PageInfo<CusDevPlan> pageInfo = new PageInfo<CusDevPlan>(selectByParams(cusDevPlanQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCusDevPlan(CusDevPlan cusDevPlan) {
        checkParams(cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        insertSelective(cusDevPlan);
        AssertUtil.isTrue(insertSelective(cusDevPlan) < 1, "计划项添加失败！");
    }

    /**
     * 更新
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan) {
        AssertUtil.isTrue(null == cusDevPlan.getId() ||
                null == selectByPrimaryKey(cusDevPlan.getId()), "待更新记录不存在");
        checkParams(cusDevPlan.getSaleChanceId(), cusDevPlan.getPlanItem(), cusDevPlan.getPlanDate());
        cusDevPlan.setUpdateDate(new Date());

        AssertUtil.isTrue(updateByPrimaryKeySelective(cusDevPlan)<1,"计划项记录更新失败");
    }

    /**
     * 删除
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCusDevPlan(Integer id) {
        CusDevPlan cusDevPlan = selectByPrimaryKey(id);
        AssertUtil.isTrue( null == id || null == cusDevPlan, "待删除记录不存在！");
        cusDevPlan.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(cusDevPlan)<1,"计划项记录删除失败");
    }

    /**
     * 效验参数非空
     *
     * @param saleChanceId
     * @param planItem
     * @param planDate
     */
    private void checkParams(Integer saleChanceId, String planItem, Date planDate) {
        System.out.println(saleChanceId);
        System.out.println(saleChanceMapper.selectByPrimaryKey(saleChanceId));
        AssertUtil.isTrue(null == saleChanceId ||
                saleChanceMapper.selectByPrimaryKey(saleChanceId) == null, "请设置营销机会Id！");
        AssertUtil.isTrue(StringUtils.isBlank(planItem), "请输入计划项内容");
        AssertUtil.isTrue(null == planDate, "请指定计划项日期！");
    }


}
