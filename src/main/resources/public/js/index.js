layui.use(['form', 'jquery', 'jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on('submit(login)', function (data) {

        var fieldData = data.field;

        if (fieldData.username == "undefined" || fieldData.username.trim() == "") {/*trim去空格*/
            layer.msg("用户名称不能为空！");
            return false;
        }
        if (fieldData.password == "undefined" || fieldData.password.trim() == "") {
            layer.msg("用户密码不能为空！");
            return false;
        }
        $.ajax({
            type: "post",
            url: ctx + "/user/login",
            data: {
                userName: fieldData.username,
                userPwd: fieldData.password
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    layer.msg("登陆成功！", function () {
                        var result = data.result;
                        $.cookie("userIdStr", result.userIdStr);
                        $.cookie("userName", result.userName);
                        $.cookie("trueName", result.trueName);

                        if ($("input[type='checkbox']").is(":checked")) {
                            $.cookie("userIdStr", result.userIdStr, {expires: 7});
                            $.cookie("userName", result.userName, {expires: 7});
                            $.cookie("trueName", result.trueName, {expires: 7});
                        }

                        window.location.href = ctx + "/main";

                    });
                } else {
                    layer.msg(data.msg, {icon: 5});
                }
            }
        });

        // console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        // console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        // console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

});