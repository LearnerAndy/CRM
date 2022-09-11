layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url: ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth: 95,
        page: true, // 开启分页
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "saleChanceListTable",
        cols: [[
            {type: "checkbox", fixed: "center"},
            {field: "id", title: '编号', fixed: "true"},
            {field: 'chanceSource', title: '机会来源', align: "center"},
            {field: 'customerName', title: '客户名称', align: 'center'},
            {field: 'cgjl', title: '成功几率', align: 'center'},
            {field: 'overview', title: '概要', align: 'center'},
            {field: 'linkMan', title: '联系人', align: 'center'},
            {field: 'linkPhone', title: '联系电话', align: 'center'},
            {field: 'description', title: '描述', align: 'center'},
            {field: 'createMan', title: '创建人', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center'},
            {field: 'uname', title: '指派人', align: 'center'},
            {field: 'assignTime', title: '分配时间', align: 'center'},
            {
                field: 'state', title: '分配状态', align: 'center', templet: function (d) {
                    return formatterState(d.state);
                }
            },
            {
                field: 'devResult', title: '开发状态', align: 'center', templet: function (d) {
                    return formatterDevResult(d.devResult);
                }
            },
            {title: '操作', templet: '#saleChanceListBar', fixed: "right", align: "center", minWidth: 150}
        ]]
    });

    /**
     * 格式化分配状态
     *  0 - 未分配
     *  1 - 已分配
     *  其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state) {
        if (state == 0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if (state == 1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }

    /**
     * 格式化开发状态
     *  0 - 未开发
     *  1 - 开发中
     *  2 - 开发成功
     *  3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value) {
        if (value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if (value == 1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if (value == 2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if (value == 3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }

    }

    /**
     * 多条件查询
     */
    $(".search_btn").click(function () {
        table.reload('saleChanceListTable', {
            where: {
                customerName: $("input[name='customerName']").val(),
                createMan: $("input[name='createMan']").val(), // 创建人
                state: $("#state").val() // 状态
            }
            , page: {
                curr: 1
            }
        });
    });
    /**
     * 头部工具栏 监听事件
     */
    table.on('toolbar(saleChances)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        console.log(checkStatus)
        switch (obj.event) {
            case 'add':
                // 点击添加按钮，打开添加营销机会的对话框
                openAddOrUpdateSaleChanceDialog();
                break;
            case 'del':
                // 点击删除按钮，将对应选中的记录删除
                deleteSaleChance(checkStatus.data);
        }

    });

    /*删除营销机会数据*/
    function deleteSaleChance(data) {
        if (data.length == 0) {
            layer.msg("请选择要删除的记录！");
            return;
        }

        // 询问用户是否确认删除
        layer.confirm("您确认要删除选中的记录吗？", {btn: ["确认", "取消"]}, function (index) {
            //关闭确认框
            layer.close(index);

            var ids = "ids="
            for (var i = 0; i < data.length; i++) {
                if (i < data.length - 1) {
                    ids += data[i].id + "&ids=";
                } else {
                    ids += data[i].id;
                }
            }

            $.ajax({
                type: "POST",
                url: ctx + "/sale_chance/delete",
                data: ids,
                dataType: "json",
                success: function (result) {
                    if (result.code == 200) {
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg, {icon: 5});
                    }
                }
            })

        });

    }

    /**
     * 表格行 监听事件
     * saleChances为table标签的lay-filter 属性值
     */
    table.on('tool(saleChances)', function (obj) {
        var data = obj.data;

        var layEvent = obj.event;

        if (layEvent == 'edit') {//编辑操作
            var saleChanceId = data.id;

            openAddOrUpdateSaleChanceDialog(saleChanceId);
        } else if (layEvent == "del") {//删除
            layer.confirm("确认要删除这条记录吗？", {icon: 3, title: '营销机会数据管理'}, function (index) {
                layer.close(index);

                $.ajax({
                    type: 'POST',
                    url:  ctx + '/sale_chance/delete',
                    data: {
                        ids: data.id
                    },
                    dataType: 'json',
                    success: function (result) {
                        if (result.code == 200) {
                            tableIns.reload();
                        } else {
                            layer.alert(result.msg, {icon: 5});
                        }
                    }
                });
            });
        }

    });

    /**
     * 打开对话框
     * @param saleChanceId
     */
    function openAddOrUpdateSaleChanceDialog(saleChanceId) {
        var title = "<h2>营销机会管理 - 机会添加</h2>"
        var url = ctx + "/sale_chance/addOrUpdateSaleChancePage"

        if (saleChanceId) {
            title = "<h2>营销机会管理 - 机会修改</h2>"
            url += "?id=" + saleChanceId;
        }

        layui.layer.open({
            title: title,
            type: 2,
            content: url,
            area: ["500px", "620px"],
            maxmin: true
        });
    }


});
