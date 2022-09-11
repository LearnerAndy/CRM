layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    form.on("submit(addOrUpdateSaleChance)", function (data) {

        var index = layer.msg("数据提交中，请稍后。。。", {
            icon: 16,
            time: false,
            shade: 0.8,
        });
        var url = ctx + "/sale_chance/save";

        if ($("input[name='id']").val()){
            url = ctx + "/sale_chance/update"
        }

        $.post(url,data.field,function (result){
            if (result.code == 200){
                layer.msg("操作成功");
                layer.close(index);
                layer.closeAll("iframe");
                parent.location.reload();
            }else{
                layer.msg(result.msg);
            }
        })
        return false;
    });

    $("#closeBtn").click(function(){
        var index = parent.layer.getFrameIndex(window.name);

        parent.layer.close(index);
    });

    /*加载下拉框*/
    $.post(ctx + "/sale_chance/user/queryAllSales",function (data) {
        // 如果是修改操作，判断当前修改记录的指派人的值
        var assignMan = $("input[name='man']").val();
        for (var i = 0; i < data.length; i++) {
            // 当前修改记录的指派人的值 与 循环到的值 相等，下拉框则选中
            if (assignMan == data[i].id) {
                $("#assignMan").append('<option value="'+data[i].id+'"selected>'+data[i].uname+'</option>');
            } else {
                $("#assignMan").append('<option value="'+data[i].id+'">'+data[i].uname+'</option>');
            }
        }
        // 重新渲染下拉框内容
        layui.form.render("select");
    });


});