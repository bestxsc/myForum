
/*
 由 lipengbiao 于 2016/10/23 更新
 edit.html 对应的 js文件，主要用于加载插件和处理各个按钮的点击操作
 */

$(document).ready(function() {
    //初始化 summernote 富文本编辑框组件
    $('.editor').summernote({
        height: 300,
        lang: 'zh-CN',
        toolbar: [
            // [groupName, [list of button]]
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['fontname', ['fontname']],
            ['font', ['strikethrough', 'superscript', 'subscript']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']]
        ],
        dialogsInBody: true
    });
});

// 发送按钮的点击事件
$('#push').click(function () {
    $('#push').attr({"disabled":"disabled"});
    $.post("publish.jsp", {"action" : "push", "theme" : $('#theme').val(), "content" : $('.editor').summernote('code')}, function(rdata){
        rdata = $.parseJSON(rdata);
        if (rdata.success){
            if (rdata.success){
                MyAlert("提示", "提交成功！");
                location.href = "index.jsp";
                $('#push').removeAttr("disabled");
            } else {
                if (rdata.e === undefined || rdata.e === ""){
                    MyAlert("错误", "操作失败，未知错误！");
                } else {
                    MyAlert("错误", rdata.e);
                }
                $('#push').removeAttr("disabled");
            }
        } else {
            if (rdata.message === undefined || rdata.message === ""){
                MyAlert("提交失败", "遇到未知错误");
                $('#push').removeAttr("disabled");
            } else {
                MyAlert("提交失败", rdata.message);
                $('#push').removeAttr("disabled");
            }
        }
    }, "text").error(function () {
        MyAlert("发送失败", "网络异常");
        $('#push').removeAttr("disabled");
    });
});

function MyAlert(title, body) {
    $("#mrtitle").text(title);
    $("#mrbody").text(body);
    $('#mymodal').modal('show');
}