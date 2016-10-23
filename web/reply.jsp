<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="my.MyCore" %>
<%@ page import="my.util.TokenManager" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="my.util.Reply" %>
<%@ page import="java.util.List" %>

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
            String rid = request.getParameter("rid");
            if (rid != null && token != null && uname != null && tm.verificateToken(token, uname)){
                try {
                    mc.deleReply(token, Integer.valueOf(rid));
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
            String nid = request.getParameter("nid");
            String context = request.getParameter("context");
            if (nid != null && token != null && uname != null && context != null
                    && context.length() > 5 && context.length() < 200 && tm.verificateToken(token, uname)){
                try {
                    mc.replyNotice(token, Integer.valueOf(nid), context);
                    json.put("success", true);
                    json.put("message", "回复成功");
                    String html = "";
                    List<Reply> lr = mc.getReplys(Integer.valueOf(nid));
                    if (lr != null){
                        for (Reply rv: lr) {
                            html = html + "<li class=\"list-group-item\"><strong>"
                                    + rv.getName() + " : </strong>" + rv.getContext();
                            if (rv.getUname().equals(uname)){
                                html = html + "<button type=\"button\" class=\"close dereply\" data-rid=\""
                                        + rv.getId() + "\"><span aria-hidden=\"true\">&times;</span><span class=\"sr-only\">Close</span></button>";
                            }
                        }
                        html += "</li>";
                    }
                    json.put("html", html);
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