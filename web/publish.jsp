<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="my.MyCore" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="my.util.TokenManager" %>

<%!
    private MyCore mc = new MyCore();
    private TokenManager tm = new TokenManager();
%>

<%
    String token = (String) session.getAttribute("token");
    String uname = (String) session.getAttribute("uname");
    response.setContentType("application/json; charset=UTF-8");
    response.setHeader("Cache-Control","no-cache");
    String action = request.getParameter("action");
    JSONObject json = new JSONObject();
    switch (action){
        case "delete":
            String nid = request.getParameter("nid");
            if (nid != null && token != null && uname != null && tm.verificateToken(token, uname)){
                try {
                    mc.deleNotice(token, Integer.valueOf(nid));
                    json.put("success", true);
                    json.put("message", "删除成功");
                    out.print(json.toString());
                } catch (Exception e){
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            } else {
                try {
                    json.put("success", false);
                    json.put("message", "传入的参数非法");
                    out.print(json.toString());
                } catch (JSONException e) {
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            }
            break;
        case "push":
            String theme = request.getParameter("theme");
            String content = request.getParameter("content");
            if (theme != null && token != null && uname != null && content != null
                    && content.length() < 5000 && theme.length() < 100 && tm.verificateToken(token, uname)){
                try {
                    mc.pushNotice(token, theme, content);
                    json.put("success", true);
                    json.put("message", "提交成功");
                    out.print(json.toString());
                } catch (Exception e){
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            } else {
                try {
                    json.put("success", false);
                    json.put("message", "发送的内容超出限制");
                    out.print(json.toString());
                } catch (JSONException e) {
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            }
            break;
        default:
            try {
                json.put("success", false);
                json.put("message", "传入的参数非法");
                out.print(json.toString());
            } catch (JSONException e) {
                out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
            }
            break;
    }
%>