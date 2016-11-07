<%--
  帖子列表页 / 主页
--%>
<%--trimDirectiveWhitespaces="true"使jsp输出的html去除多余空行 --%>
<%--这种写法可以让包在多个页面中生效使用 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%--导入包 --%>>
<%@ page import="my.MyCore" %>
<%@ page import="my.util.TokenManager" %>
<%@ page import="java.util.List" %>
<%@ page import="my.util.Notices" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="my.util.Reply" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.FormatStyle" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.time.ZoneId" %>

<%!
    private TokenManager tm = new TokenManager();
    private MyCore mc = new MyCore();
    private DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.CHINA)
                    .withZone( ZoneId.systemDefault());
    private static final int SHOW_NUMBER = 5;
%>

<%-- 取得会话信息 --%>
<%
    String token = (String) session.getAttribute("token");
    String uname = (String) session.getAttribute("uname");
    if (token == null || uname == null || !tm.verificateToken(token, uname)){
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <!-- 下述代码的意思是告诉ie以最高级模式渲染文档，避免了版本升级造成影响。 -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>主页 - myForum</title>

    <!-- Bootstrap -->
    <link href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/index.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">myForum</a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#">轻量级在线公告板（开发版）</a></li>
                    <li><a href="edit.html">发布</a></li>
                    <%
                        if (tm.uidByToken(token).equals("1")){
                    %>
                        <li><a href="umen.html">用户管理</a></li>
                    <%
                        }
                    %>
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </nav>
    <div class="container">
        <%
            int repage = 1;
            if (request.getParameter("page") != null){
                repage = Integer.valueOf(request.getParameter("page"));
            }
            int count = mc.getNoticeCount(token);
            if (count <= 0){
        %>
                <div class="null-template">
                    <h1>（╯－＿－）╯╧╧ Sorry</h1>
                    <p class="lead">似乎是服务器罢工了，没有任何内容可供我们前端显示...</p>
                </div>
        <%
                return;
            }
            int pages = count / SHOW_NUMBER;
            if (count % SHOW_NUMBER > 0) pages++;
            ArrayList<Integer> pagenumber = new ArrayList<>();
            pagenumber.add(repage);
            for (int i = 1; i < 3; i++){
                if (repage - i > 0){
                    pagenumber.add(0, repage - i);
                }
            }
            for (int i = 1; i < 3; i++){
                if (repage + i <= pages){
                    pagenumber.add(repage + i);
                }
            }
            List<Notices> ln = mc.getNotices((repage - 1) * 5, 5, token);
            if (ln != null && tm.verificateToken(token, uname)){
        %>
                <nav>
                    <ul class="pagination">
                        <li><a href="?<%="page=" + 1%>">&laquo;</a></li>
                        <%
                            for (Integer i: pagenumber) {
                        %>
                            <li><a href="?<%="page=" + i%>"><%=i%></a></li>
                        <%
                            }
                        %>
                        <li><a href="?<%="page=" + pages%>">&raquo;</a></li>
                    </ul>
                </nav>
        <%
                for (Notices v: ln) {
        %>
                    <div class="panel panel-default">
                        <div class="panel-heading"><%=v.getTheme()%>
                        <%
                            if (v.getUname().equals(uname)){
                        %>
                            <button type="button" class="close denotice" data-nid="<%=v.getId()%>"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                        <%
                            }
                        %>
                        </div>
                        <div class="panel-body">
                            <p><%=v.getContent()%></p>
                        </div>
                        <div class="panel-footer">
                            <div class="row">
                                <div class="col-md-10 noticedate">
                                    <%=formatter.format(v.getTimestamp())%><strong>  由</strong> <a href="#"><%=v.getName()%></a> <strong>发送</strong>
                                </div>
                                <div class="col-md-2">
                                    <button type="button" class="btn btn-info btn-block breply" data-nid="<%=v.getId()%>">回复</button>
                                </div>
                            </div>
                        </div>
                        <ul class="list-group">
                            <%
                                List<Reply> lr = mc.getReplys(v.getId());
                                if (lr != null){
                                    for (Reply rv: lr) {
                            %>
                                        <li class="list-group-item"><strong><%=rv.getName()%> : </strong><%=rv.getContext()%>
                                            <%
                                                if (rv.getUname().equals(uname)){
                                            %>
                                                <button type="button" class="close dereply" data-rid="<%=rv.getId()%>"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                            <%
                                                }
                                            %>
                                        </li>
                            <%
                                    }
                                }
                            %>
                        </ul>
                    </div>
        <%
                }
        %>
                <nav>
                    <ul class="pagination">
                        <li><a href="?<%="page=" + 1%>">&laquo;</a></li>
                        <%
                            for (Integer i: pagenumber) {
                        %>
                        <li><a href="?<%="page=" + i%>"><%=i%></a></li>
                        <%
                            }
                        %>
                        <li><a href="?<%="page=" + pages%>">&raquo;</a></li>
                    </ul>
                </nav>
        <%
            } else {
        %>
                <div class="null-template">
                    <h1>（╯－＿－）╯╧╧ Sorry</h1>
                    <p class="lead">似乎是服务器罢工了，没有任何内容可供我们前端显示...</p>
                </div>
        <%
            }
        %>
    </div>

    <div id="mymodal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h2 id="mrtitle" class="modal-title">提示</h2>
                </div>
                <div id="mrbody" class="modal-body">
                    <p>&hellip;</p>
                </div>
                <div class="modal-footer">
                    <button id="mrb1" type="button" class="btn btn-primary" data-dismiss="modal">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

    <div id="reply" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h2 id="rtitle" class="modal-title">回复</h2>
                </div>
                <div id="rbody" class="modal-body">
                    <label for="reply_ta"></label><textarea id="reply_ta" class="form-control" rows="3"></textarea>
                </div>
                <div class="modal-footer">
                    <span id="r_t" style="color: #ff5138;padding-right: 10px"></span>
                    <button id="rb1" type="button" class="btn btn-primary">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

    <footer class="footer hidden-print">
        <div class="copy-right">
            <span>Developer Edition, Maintenance by lipengbiao.</span>
        </div>
    </footer>
</body>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="http://cdn.bootcss.com/jquery/2.2.4/jquery.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="http://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="js/index.js"></script>
</html>
