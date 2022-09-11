package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    private UserRoleMapper userRoleMapper;

    /**
     * 营销机会删除
     * @param ids
     */
    public void deleteSaleChance(Integer[] ids){
        AssertUtil.isTrue(null == ids || ids.length == 0,"请选择需要删除的数据！");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)<0,"营销机会数据删除失败！");

    }


    /**
     * 多条件分页查询营销机会 (BaseService 中有对应的方法)
     * @param query
     * @return
     */
    public Map<String, Object> querySaleChanceByParams (SaleChanceQuery query) {
        Map<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<SaleChance> pageInfo =
                new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 营销机会数据添加
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        //参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //设置相关参数默认值
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }else {
            saleChance.setAssignTime(new Date());
            saleChance.setState(1);
            saleChance.setDevResult(1);
        }
        AssertUtil.isTrue(insertSelective(saleChance)<1,"营销机会数据添加失败！");
    }

    /**
     * 修改数据
     *      1.校验参数
     *          id属性是必须存在的，查询数据库校验
     *          customerName   客户名称 非空
     *          linkMan       联系人   非空
     *          linkPhone      手机号码 非空  手机号11位正则校验
     *      2.默认值
     *          update_date  修改时间
     *          判断是否指派了工作人员
     *              1.修改前没有分配人
     *                  修改后没有分配人
     *                      不做任何操作
     *                  修改后有分配人
     *                      dev_result  开发状态
     *                      assign_time 分配时间
     *                      state       分配状态
     *              2.修改前有分配人
     *                  修改后没有分配人
     *                      assign_time 分配时间 null
     *                      dev_result  开发状态
     *                      state       分配状态 0
     *                  修改后有分配人
     *                      判断更改后的人员和更改前的人员有没有变动
     *                          没有变动不做操作
     *                          有变动，assign_time最新的时间
     *     3.执行修改操作，判断是否修改成功
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(null == temp,"待更新记录不存在！");
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(temp.getAssignMan())){
            if (StringUtils.isNotBlank(saleChance.getAssignMan())){
                saleChance.setAssignTime(new Date());
                saleChance.setState(1);
                saleChance.setDevResult(1);
            }
        }else {
            if (StringUtils.isBlank(saleChance.getAssignMan())){
                saleChance.setAssignTime(null);
                saleChance.setState(0);
                saleChance.setDevResult(0);
            }else {
                if (!temp.getAssignMan().equals(saleChance.getAssignMan())){
                    saleChance.setAssignTime(new Date());

                }
            }
        }
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"营销数据修改失败");
    }
    /**
     * 数据添加参数效验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入手机号！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号格式不正确");
    }

    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     */
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        SaleChance temp = selectByPrimaryKey(id);
        AssertUtil.isTrue(null == id || null == temp,"待更新记录不存在！");
        temp.setDevResult(devResult);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"机会数据更新失败！");

    }
}