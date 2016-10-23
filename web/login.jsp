<%--
  登录页面
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="my.MyCore" %>

<%! private MyCore core = new MyCore(); %>

<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录 - myForum</title>

    <!-- Bootstrap -->
    <link href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/login.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

<div class="container">
    <header class="page-header">
        <div class="row">
            <div class="col-md-10">
                <h1>myForum
                    <small>轻量级在线公告板</small>
                </h1>
            </div>
            <div class="col-md-2">
                <span style="color: #575c8f">服务器实时负载：</span><span id="cpu-perproty" class="updating-chart">0,5,1,4,8,9,7,5,4,1,2,4</span>
            </div>
        </div>

        <hr>
    </header>
    <form class="form-signin" role="form" action="login.jsp" method="post">
        <h1 class="form-signin-heading">登录</h1>
        <input class="form-control" placeholder="UName" type="text" name="uname" required autofocus>
        <input class="form-control" placeholder="Password" type="password" name="password" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
        <%
            String uname = request.getParameter("uname");
            String password = request.getParameter("password");
            if (uname != null && password != null && (!uname.isEmpty() || !password.isEmpty())){
                try {
                    session.setAttribute("uname", uname);
                    session.setAttribute("token", core.authQuery(uname, password));
        %>
                    <div class="alert alert-success" role="alert">
                        <strong>成功！</strong>正在跳转...
                    </div>
        <%
                    response.sendRedirect("index.jsp");
                } catch (Exception e){
        %>
                    <div class="alert alert-danger" role="alert">
                        <strong>失败！</strong><%=e.getMessage()%>
                    </div>
        <%
                }
            }
        %>
    </form>
    <footer class="footer hidden-print">
        <div class="copy-right">
            <span>Developer Edition, Maintenance by lipengbiao.</span>
        </div>
    </footer>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="http://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="js/peity.min.js"></script>

<script>
    //仪表盘
    $(document).ready(function () {
        // Updating charts.
        var p_cpu = $("#cpu-perproty").peity("line", { width: 128 })

        setInterval(function() {
            var random = Math.round(Math.random() * 10);
            var values = p_cpu.text().split(",");
            values.shift();
            values.push(random);
            p_cpu
                    .text(values.join(","))
                    .change();
        }, 1500)
    })
</script>

</body>
</html>
