<%@ page import="org.json.JSONObject" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    response.setContentType("application/json; charset=UTF-8");
    response.setHeader("Cache-Control","no-cache");
    String action = request.getParameter("action");
    JSONObject json = new JSONObject();
    if (action == null) action = "";
    switch (action){
        case "cpu":
            out.print(json.toString());
            return;
        default:
            out.print("{'success' : false}");
            return;
    }
%>
