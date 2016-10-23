
/*
由 lipengbiao 于 2016/10/23 更新
index.jsp 对应的 js文件，主要用于处理各个按钮的点击操作
 */

//缓存和传递被点击的回复按钮元素
var re_this;
$(document).ready(function () {
    //委托 回复删除按钮 的点击事件
    $(".list-group").delegate(".dereply", "click",function () {
        var kthis = $(this);
        $(this).hide();
        $.post("reply.jsp", {"action" : "delete", "rid" : kthis.data("rid")}, function(rdata){
            kthis.show();
            rdata = $.parseJSON(rdata);
            if (rdata.success){
                MyAlert("提示", "删除成功！");
                kthis.parent().remove();
            } else {
                if (rdata.e === undefined || rdata.e === ""){
                    MyAlert("错误", "操作失败，未知错误！");
                } else {
                    MyAlert("错误", rdata.e);
                }
            }
        }, "text").error(function () {
            MyAlert("错误", "提交失败！");
        });
    });
    //委托 删除公告按钮 的点击事件
    $(".panel-heading").delegate(".denotice", "click", function () {
        var kthis = $(this);
        $(this).hide();
        $.post("publish.jsp", {"action" : "delete", "nid" : kthis.data("nid")}, function(rdata){
            kthis.show();
            rdata = $.parseJSON(rdata);
            if (rdata.success){
                MyAlert("提示", "删除成功！");
                kthis.parent().parent().remove();
            } else {
                if (rdata.message === undefined || rdata.message === ""){
                    MyAlert("错误", "操作失败，未知错误！");
                } else {
                    MyAlert("错误", rdata.message);
                }
            }
        }, "text").error(function () {
            MyAlert("错误", "提交失败！");
        });
    });
    //委托 回复按钮 的点击事件
    $(".panel-footer").delegate(".breply", "click", function () {
        $('#reply').modal('show');
        re_this = $(this);
    })
});

//模态回复框 确定 按钮被点击时
$("#rb1").click(function () {
    var r_t = $("#r_t");
    var ta = $("#reply_ta");
    var kthis = $(this);
    ta.focus();
    kthis.attr({"disabled":"disabled"});
    var t = ta.val();
    if (t.length < 5){
        r_t.text("回复内容太短");
        ta.focus();
        kthis.removeAttr("disabled");
        return;
    } else if (t.length > 100){
        r_t.text("回复内容太长");
        ta.focus();
        kthis.removeAttr("disabled");
        return;
    }
    //发送回复请求
    $.post("reply.jsp", {"action" : "push", "nid" : re_this.data("nid"), "context" : t}, function(rdata){
        rdata = $.parseJSON(rdata);
        if (rdata.success){
            if (rdata.html != undefined){
                re_this.parent().parent().parent().next().html(rdata.html);
            } else {
                location.reload();
            }
            kthis.removeAttr("disabled");
            $('#reply_ta').val("");
            $('#reply').modal('hide');
        } else {
            if (rdata.message === undefined || rdata.message === ""){
                $("#r_t").text("发送失败，遇到未知错误");
                kthis.removeAttr("disabled");
            } else {
                $("#r_t").text("发送失败：" + rdata.message);
                kthis.removeAttr("disabled");
            }
        }
    }, "text").error(function () {
        $("#r_t").text("提交失败，请检查网络");
        kthis.removeAttr("disabled");
    });

});

//模态信息框
function MyAlert(title, body) {
    $("#mrtitle").text(title);
    $("#mrbody").text(body);
    $('#mymodal').modal('show');
}