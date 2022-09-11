layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var tableIns = table.render({
        elem: '#cusDevPlanList',
        url: ctx + '/cus_dev_plan/list?sid=' + $("input[name='id']").val(),
        cellMinWidth: 95,
        page: true,
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "cusDevPlanListTable",
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'planItem', title: '计划项', align: "center"},
            {field: 'exeAffect', title: '执行效果', align: "center"},
            {field: 'planDate', title: '执行时间', align: "center"},
            {field: 'createDate', title: '创建时间', align: "center"},
            {field: 'updateDate', title: '更新时间', align: "center"},
            {title: '操作', fixed: "right", align: "center", minWidth: 150, templet: "#cusDevPlanListBar"}
        ]]
    });

    /**
     * 打开“添加或更新客户开发计划”对话框
     */
    function openAddOrUpdateCusDevPlanDialog(id) {
        var title = "计划项管理-添加计划项";
        var url = ctx + "/cus_dev_plan/addOrUpdateCusDevPlanPage?sid=" + $("input[name='id']").val();
        if (id) {
            url += "&id=" + id;
            title = "计划项管理-更新计划项";
        }
        layui.layer.open({
            title: title,
            type: 2,
            area: ["500px", "300px"],
            content: url,
            maximin: true
        });
    }

    /**
     * 更新营销机会的状态
     * @param devResult
     */
    function updateSaleChanceDevResult(devResult) {
        var sid = $("input[name='id']").val();

        layer.confirm("确认执行当前操作？", {icon: 3, title: "计划项维护"}, function (index) {
            $.post(ctx + "/sale_chance/updateSaleChanceDevResult", {id: sid, devResult: devResult}, function (data) {
                if (data.code == 200) {
                    layer.msg("操作成功！");
                    layer.closeAll("iframe");
                    parent.layer.close();
                } else {
                    layer.msg(data.msg, {icon: 5});
                }
            });
        });

    }

    /**
     * 头部工具栏事件监听
     */
    table.on("toolbar(cusDevPlans)", function (obj) {
        switch (obj.event) {
            case "add":
                openAddOrUpdateCusDevPlanDialog();
                break;
            case "success":
                updateSaleChanceDevResult(2);
                break;
            case "failed":
                updateSaleChanceDevResult(3);
                break;
        }
    });
    /**
     * 行工具栏事件监听
     */
    table.on("tool(cusDevPlans)", function (obj) {
        var layEvent = obj.event;
        if (layEvent === "edit") {
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        } else if (layEvent === "del") {
            layer.confirm("确认删除当前数据？", {icon: 3, title: "开发管理计划"}, function (index) {
                $.post(ctx + "/cus_dev_plan/delete", {id: obj.data.id}, function (data) {
                    if (data.code == 200) {
                        layer.msg("操作成功！");
                        tableIns.reload();
                    } else {
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            });
        }
    });


});
