$(function () {
    loadModuleInfo(); 
});

/**
 * 加载ztree结构
 */
var zTreeObj;
function loadModuleInfo() {
    $.ajax({
        type:"post",
        url:ctx+"/module/queryAllModules",
        data:{
            roleId:$('input[name="roleId"]').val()
        },
        dataType:"json",
        success:function (data) {

            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onCheck: zTreeOnCheck
                }
            };
            zTreeObj = $.fn.zTree.init($("#test1"), setting, data);
        }
    })
}

/**
 * 监听多选框选中状态
 * @param event
 * @param treeId
 * @param treeNode
 */
function zTreeOnCheck(event, treeId, treeNode) {
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    /**
     * 获取节点集合（）
     *
     * 1. 提升参数作用域
     * 2. 循环拼接mIds
     * 3. ajaxs
     *    1. url 接收隐藏域id
     */
    var nodes= zTreeObj.getCheckedNodes(true);
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }

    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+$("input[name='roleId']").val(),
        dataType:"json",
        success:function (data) {
            console.log(data);
        }
    })

}