<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page import="my.MyCore" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="java.util.List" %>
<%@ page import="my.util.Person" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="my.util.TokenManager" %>
<%@ page import="java.util.ArrayList" %>

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
    if (action == null) action = "";
    switch (action){
        case "get":
            String pageSize = request.getParameter("limit");
            String pageIndex = request.getParameter("offset");
            String Group = request.getParameter("Group");
            String Name = request.getParameter("Name");
            String Uid = request.getParameter("Uid");
            String UName = request.getParameter("UName");

            if ((pageIndex != null || !pageIndex.isEmpty()) &&
                    (token != null || !token.isEmpty()) &&
                    (uname != null || !uname.isEmpty()) &&
                    (pageSize != null || !pageSize.isEmpty()) &&
                    tm.verificateToken(token, uname) &&
                    tm.uidByToken(token).equals("1")){
                try {
                    List<Person> l = mc.getPersons(Uid, UName, Name, Group, Integer.valueOf(pageIndex) * Integer.valueOf(pageSize), Integer.valueOf(pageSize));
                    JSONArray rows = new JSONArray();
                    for (Person aL : l) {
                        JSONObject jt = new JSONObject();
                        jt.put("Uid", aL.getUid());
                        jt.put("UName", aL.getUname());
                        jt.put("Name", aL.getName());
                        jt.put("Group", aL.getGroup());
                        jt.put("Publishing", aL.isPublishing());
                        jt.put("Reply", aL.isReply());
                        rows.put(jt);
                    }
                    json.put("rows", rows);
                    json.put("total", l.size());
                    out.print(json.toString());
                } catch (Exception e){
                    out.print("{\"total\" : 0, \"rows\" : []}");
                }
            } else {
                try {
                    json.put("rows", new JSONArray());
                    json.put("total", 0);
                    out.print(json.toString());
                } catch (JSONException e) {
                    out.print("{\"total\" : 0, \"rows\" : []}");
                }
            }
            return;

        case "set":
            String sName = request.getParameter("Name");
            String sUid = request.getParameter("Uid");
            String sUName = request.getParameter("UName");
            String sPass = request.getParameter("Password");
            String sPu = request.getParameter("Publishing");
            String sRe = request.getParameter("Reply");

            if ((sName != null || !sName.isEmpty()) &&
                    (token != null || !token.isEmpty()) &&
                    (sUid != null || !sUid.isEmpty()) &&
                    (sUName != null || !sUName.isEmpty()) &&
                    (sPass != null || !sPass.isEmpty()) &&
                    (sPu != null || !sPu.isEmpty()) &&
                    (sRe != null || !sRe.isEmpty())&&
                    tm.verificateToken(token, uname) &&
                    tm.uidByToken(token).equals("1")){
                try {
                    mc.setUser(token,sUid,sUName,sName,sPass,"0",sPu,sRe);
                    json.put("success", true);
                    json.put("message", "修改成功");
                    out.print(json.toString());
                } catch (Exception e){
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            }
            return;

        case "add":
            String aName = request.getParameter("Name");
            String aUName = request.getParameter("UName");
            String aPass = request.getParameter("Password");
            String aPu = request.getParameter("Publishing");
            String aRe = request.getParameter("Reply");

            if ((aName != null || !aName.isEmpty()) &&
                    (token != null || !token.isEmpty()) &&
                    (aUName != null || !aUName.isEmpty()) &&
                    (aPass != null || !aPass.isEmpty()) &&
                    (aPu != null || !aPu.isEmpty()) &&
                    (aRe != null || !aRe.isEmpty()) &&
                    tm.verificateToken(token, uname) &&
                    tm.uidByToken(token).equals("1")){
                try {
                    mc.addUser(token, aUName, aPass, aName, aPu.equals("true"), aRe.equals("true"));
                    json.put("success", true);
                    json.put("message", "添加成功");
                    out.print(json.toString());
                } catch (Exception e){
                    out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
                }
            }
            return;
        case "dele":
            String ids = request.getParameter("ids");
            if (ids != null && !ids.isEmpty()){
                String[] s = ids.split(",", 10);
                ArrayList<Integer> ai = new ArrayList<>();
                for (String v : s){
                    if (Integer.valueOf(v) > 1){
                        ai.add(Integer.valueOf(v));
                    }
                }
                try {
                    mc.deleUser(token, ai);
                    json.put("success", true);
                    json.put("message", "删除成功");
                    out.print(json.toString());
                } catch (Exception e) {
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
            return;
        default:
            try {
                json.put("success", false);
                json.put("message", "传入的参数非法");
                out.print(json.toString());
            } catch (JSONException e) {
                out.print("{\"success\" : false, \"message\" : \"" + e.getMessage() + "\"}");
            }
            return;
    }
%>