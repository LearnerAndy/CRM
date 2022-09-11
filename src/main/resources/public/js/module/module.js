layui.use(['table', 'treetable'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treeTable = layui.treetable;

    // 渲染表格
    treeTable.render({
        treeColIndex: 1,
        treeSpid: -1,
        treeIdName: 'id',
        treePidName: 'parentId',
        elem: '#munu-table',
        url: ctx+'/module/list',
        toolbar: "#toolbarDemo",
        treeDefaultClose:true,
        page: true,
        cols: [[
            {type: 'numbers'},
            {field: 'moduleName', minWidth: 100, title: '菜单名称'},
            {field: 'optValue', title: '权限码'},
            {field: 'url', title: '菜单url'},
            {field: 'createDate', title: '创建时间'},
            {field: 'updateDate', title: '更新时间'},
            {
                field: 'grade', width: 80, align: 'center', templet: function (d) {
                    if (d.grade == 0) {
                        return '<span class="layui-badge layui-bg-blue">目录</span>';
                    }
                    if(d.grade==1){
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                    if (d.grade == 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#auth-state', width: 240, align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });


    //监听工具条
    table.on('tool(munu-table)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'add') {
            if(data.grade==2){
                layer.msg("暂不支持四级菜单添加操作!");
                return;
            }
            openAddModuleDialog(data.grade+1,data.id);
        } else if (layEvent === 'edit') {
            // 记录修改
            openUpdateModuleDialog(data.id);
        } else if (layEvent === 'del') {
            layer.confirm('确定删除当前菜单？', {icon: 3, title: "菜单管理"}, function (index) {
                $.post(ctx+"/module/delete",{id:data.id},function (data) {
                    if(data.code==200){
                        layer.msg("操作成功！");
                        window.location.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                });
            })
        }
    });

    table.on('toolbar(munu-table)', function(obj){
        switch(obj.event){
            case "expand":
                treeTable.expandAll('#munu-table');
                break;
            case "fold":
                treeTable.foldAll('#munu-table');
                break;
            case "add":
                openAddModuleDialog(0,-1);
                break;
        };
    });


    // 打开添加菜单对话框
    function openAddModuleDialog(grade,parentId){
        var grade=grade;
        var url  =  ctx+"/module/addModulePage?grade="+grade+"&parentId="+parentId;
        var title="菜单添加";
        layui.layer.open({
            title : title,
            type : 2,
            area:["700px","450px"],
            maxmin:true,
            content : url
        });
    }

    function openUpdateModuleDialog(id){
        var url  =  ctx+"/module/updateModulePage?id="+id;
        var title="菜单更新";
        layui.layer.open({
            title : title,
            type : 2,
            area:["700px","450px"],
            maxmin:true,
            content : url
        });
    }
});