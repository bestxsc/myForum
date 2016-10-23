
/*
 由 lipengbiao 于 2016/10/23 更新
 edit.html 对应的 js文件，主要用于加载插件和处理各个按钮的点击操作
 */

//缓存元素
var $table = $('#usertable');
var $remove = $('#btn_delete');
var $add = $('#btn_add');
var $mwin = $('#modal_edit');
selections = [];

$(document).ready(function () {
    //1.初始化Bootstrap - Table
    var oTable = new TableInit();
    oTable.Init();

    //修改和删除共用一个模态编辑窗口，分情况判断
    $('#rb1').click(function () {
        //添加模式
        if ($(this).data("model") == 'add'){
            $.post('user.jsp', {'action' : 'add', 'UName' : $('#inputUName').val(), 'Password' : $('#inputPassword').val(), 'Name' : $('#inputName').val(), 'Publishing' : $('#cbPublish').is(':checked'), 'Reply' : $('#cbReply').is(':checked')}, function (rdata) {
                rdata = $.parseJSON(rdata);
                if (rdata.success){
                    $('#modal_edit').modal('hide');
                    $table.bootstrapTable('refresh');
                } else {
                    if (rdata.message === undefined || rdata.message === ""){
                        $("#r_t").text("发送失败，遇到未知错误");
                    } else {
                        $("#r_t").text("发送失败：" + rdata.message);
                    }
                }
            }, "text").error(function () {
                $("#r_t").text("提交失败，请检查网络");
            })
        }
        //修改模式
        if ($(this).data('model') == 'set'){
            $.post('user.jsp', {'action' : 'set', 'Uid' : $(this).data('uid'), 'UName' : $('#inputUName').val(), 'Password' : $('#inputPassword').val(), 'Name' : $('#inputName').val(), 'Publishing' : $('#cbPublish').is(':checked'), 'Reply' : $('#cbReply').is(':checked')}, function (rdata) {
                rdata = $.parseJSON(rdata);
                if (rdata.success){
                    $('#modal_edit').modal('hide');
                    $table.bootstrapTable('refresh');
                } else {
                    if (rdata.message === undefined || rdata.message === ""){
                        $("#r_t").text("发送失败，遇到未知错误");
                    } else {
                        $("#r_t").text("发送失败：" + rdata.message);
                    }
                }
            }, "text").error(function () {
                $("#r_t").text("提交失败，请检查网络");
            })
        }
    });
});

//当网页窗口改变时，表格高度改变
$(window).resize(function () {
    $('#tableId').bootstrapTable('resetView');
    $table.bootstrapTable('resetView', {
        height: getHeight()
    });
});

//表格的各项的设置和初始化过程
var TableInit = function () {
    var oTableInit = {};
    //初始化Table
    oTableInit.Init = function () {
        $table.bootstrapTable({
            searchTimeOut: 1000,
            height: getHeight(),
            url: 'user.jsp',   //请求后台的URL（*）
            method: 'post',      //请求方式（*）
            contentType: "application/x-www-form-urlencoded",
            toolbar: '#toolbar',    //工具按钮用哪个容器
            striped: true,      //是否显示行间隔色
            cache: false,      //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,     //是否显示分页（*）
            sortable: false,      //是否启用排序
            sortOrder: "asc",     //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            sidePagination: "server",   //分页方式：client客户端分页，server服务端分页（*）
            pageNumber:1,      //初始化加载第一页，默认第一页
            pageSize: 10,      //每页的记录行数（*）
            pageList: [10, 25, 50, 100],  //可供选择的每页的行数（*）
            search: false,      //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            strictSearch: true,
            showColumns: true,     //是否显示所有的列
            showRefresh: true,     //是否显示刷新按钮
            minimumCountColumns: 2,    //最少允许的列数
            uniqueId: "Uid",      //每一行的唯一标识，一般为主键列
            idField: "Uid",
            showToggle: false,     //是否显示详细视图和列表视图的切换按钮
            cardView: false,     //是否显示详细视图
            detailView: false,     //是否显示父子表
            columns: [{
                field: 'state',
                checkbox: true,
                align: 'center',
                valign: 'middle'
            },{
                field: 'Uid',
                title: 'UID',
                align: 'center'
            }, {
                field: 'Group',
                    title: '用户组',
                    align: 'center'
            }, {
                field: 'UName',
                    title: '用户名',
                    align: 'center'
            }, {
                field: 'Name',
                    title: '姓名',
                    align: 'center'
            }, {
                field: 'Publishing',
                    title: '发布权限',
                    align: 'center'
            }, {
                field: 'Reply',
                    title: '回复权限',
                    align: 'center'
            }, {
                field: 'operate',
                    title: '操作',
                    align: 'center',
                    events: operateEvents,
                    formatter: operateFormatter
            }]
        });
    };
    //得到查询的参数
    oTableInit.queryParams = function (params) {
        return { //这里的键的名字和控制器的变量名必须一致
            limit: params.limit, //页面大小
            offset: params.offset, //页码
            search: params.search,
            sort: params.sort,
            order: params.order,
            Group: $("#txt_search_group").val(),
            Name: $("#txt_search_name").val(),
            //Uid: params.Uid,
            //UName: params.UName,
            //Publishing: params.Publishing,
            //Reply: params.Reply,
            action: "get"
        };
    };

    return oTableInit;
};

// 表格选中框的事件绑定
$table.on('check.bs.table uncheck.bs.table ' +
    'check-all.bs.table uncheck-all.bs.table', function () {
    $remove.prop('disabled', !$table.bootstrapTable('getSelections').length);
    // save your data, here just save the current page
    selections = getIdSelections();
});

// 删除按钮被点击时
$remove.click(function () {
    var ids = getIdSelections();
    if(ids.length == 0) return;
    $remove.prop('disabled', true);
    $.post('user.jsp', {'action' : 'dele', 'ids' : ids.toString()}, function (rdata) {
        rdata = $.parseJSON(rdata);
        if (rdata.success){
            $table.bootstrapTable('refresh');
            $remove.prop('disabled', true);
        } else {
            if (rdata.message === undefined || rdata.message === ""){
                alert("发送失败，遇到未知错误");
                $remove.prop('disabled', true);
            } else {
                alert("发送失败：" + rdata.message);
                $remove.prop('disabled', true);
            }
        }
    }, 'text').error(function () {
        alert("删除失败！");
        $remove.prop('disabled', true);
    });
});

//添加按钮被点击时
$add.click(function () {
    $('#inputUName').val("");
    $('#inputPassword').val("");
    $('#inputName').val("");
    $('#rb1').data("model", "add");
    $mwin.modal('show');
});

//操作列的修改按钮构造器
function operateFormatter(value, row, index) {
    return [
        '<a class="edit ml10" href="javascript:void(0)" title="Edit">',
        '<i class="glyphicon glyphicon-edit"></i>',
        '</a>'
    ].join('');
}

//操作列的修改按钮的点击事件构造器
window.operateEvents = {
    'click .edit': function (e, value, row, index) {
        var a = $('#inputUName');
        a.val("");
        var b = $('#inputPassword');
        b.val("");
        var c = $('#inputName');
        c.val("");
        a.attr("placeholder", '- 不修改 -');
        b.attr("placeholder", '- 不修改 -');
        c.attr("placeholder", '- 不修改 -');

        $('#rb1').data("model", "set");
        $('#rb1').data("uid", row.Uid);
        $('#cbPublish').attr("checked", row.Publishing);
        $('#cbReply').attr("checked", row.Reply);
        $mwin.modal('show');
    }
};

function getHeight() {
    return $(window).height() - $('.page-header').outerHeight(true) - $('.navbar').outerHeight(true) - $('#formSearch').outerHeight(true);
}

function getIdSelections() {
    return $.map($table.bootstrapTable('getSelections'), function (row) {
        return row.Uid;
    });
}
