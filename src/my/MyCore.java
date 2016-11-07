package my;

import my.util.Notices;
import my.util.Person;
import my.util.Reply;
import my.util.TokenManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Queable;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class MyCore {
    static private Logger mlog;
    private Connection con = null;//Connection不是一个接口么,为什么这里当作一个类
    private TokenManager tm = null;
    private static final String JDBC_MYSQL_URL = "jdbc:mysql://localhost:3306/ceshi?useUnicode=true&characterEncoding=utf8";
    private static final String JDBC_MYSQL_UNAME = "root";
    private static final String JDBC_MYSQL_PASS = "root";

    static {//这个是方法还是？
        mlog = Logger.getLogger("my.MyCore");
    }

    public MyCore(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
//          连接到数据库的状态用con来存储
            con = DriverManager.getConnection(JDBC_MYSQL_URL, JDBC_MYSQL_UNAME, JDBC_MYSQL_PASS);
//          初始化tokenmanager类的tm
            tm = new TokenManager();
//          日志中添加“成功连接到数据库！”
            mlog.info("成功连接数据库！");
        } catch (Exception e) {
//          日志中添加“数据库连接失败”和异常信息
        	mlog.warning("数据库连接失败：" + e.getMessage());
//        	在命令行打印错误信息
            e.printStackTrace();
        }
    }
//b=boolean，判断uid等的真假
    private static String someSQL(boolean buid, boolean buname, boolean bname, boolean bgroup){
        String sql = "SELECT * FROM user WHERE";
        if (buid || buname || bname || bgroup){
            if (buid){
                if (buname || bname || bgroup){
//                	问号的含义是什么
                    sql += " user.uid = ? AND";
                } else {
                    sql += " user.uid = ?";
                }
            }
            if (buname){
                if (bname || bgroup){
                    sql += " user.uname LIKE ? AND";
                } else {
                    sql += " user.uname LIKE ?";
                }
            }
            if (bname){
                if (bgroup){
                    sql += " user.name LIKE ? AND";
                } else {
                    sql += " user.name LIKE ?";
                }
            }
            if (bgroup){
                sql += " user.group = ?";
            }
        } else {
            sql = "SELECT * FROM user";
        }
        return sql + " LIMIT ?,?";
    }

    public List<Person> getPersons(String uid, String uname, String name, String group, int page, int offset){
//    	不明白
        String sql = someSQL(!(uid == null || uid.isEmpty()),
                !(uname == null || uname.isEmpty()),
                !(name == null || name.isEmpty()),
                !(group == null || group.isEmpty()));

//      经过预编译的statement，进行数据库的操作
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Person> L = new ArrayList<>();
//		Queue是队列的意思，是Linkedlist的父接口
        Queue<String>  Q = new LinkedList<>();
        if (!(uid == null || uid.isEmpty())) Q.add(uid);
        if (!(uname == null || uname.isEmpty())) Q.add(uname);
        if (!(name == null || name.isEmpty())) Q.add(name);
        if (!(group == null || group.isEmpty())) Q.add(group);
//		如果con为空或者缓冲池链接成功或者page小于0或者offset（元素相对于文档的偏移，即位置）小于0，那么返回L（为什么返回L？）
        if (con == null || !tm.state || page < 0 || offset < 0) return L;
        try {
//          设置预处理语句
        	ps = con.prepareStatement(sql);
            int i = 1;
//          当Q不为空是开始循环
            while (!Q.isEmpty()){
//            	将i赋给Q.poll；Q.pool方法的作用是获取并移除此列的头，如果队列为空则返回null。
                ps.setString(i, Q.poll());
                i++;
            }
//          给page设定i这个参数
            ps.setInt(i, page);
            ps.setInt(++i, offset);
//          在ps对象内执行sql查询，返回查询值
            rs = ps.executeQuery();
//          当有下一个值时
            while (rs.next()) {
//            	list中添加一个新的person，参数为下标为""处的整型值 --->getInt方法：获取下表uid处的整型值
                L.add(new Person(rs.getInt("uid"),
                        rs.getString("uname"),
                        rs.getString("name"),
                        rs.getInt("group"),
                        rs.getBoolean("publishing"),
                        rs.getBoolean("reply")));
            }
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            return L;
        } finally {
            try {
//            	预处理不为空时关闭数据库
                if (ps != null) {
                    ps.close();
                }
//              如果resultset为空时，关闭数据库 --->resultset.close方法：立即释放指向的数据库或JDBC资源
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning("List - " + e.getMessage());
            }
        }
        return L;
    }

    private Map<String,String> getPerson(int uid){
        String sql = "SELECT * FROM user WHERE user.uid = ? LIMIT 1";//这个问号的值怎么被替换？
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, String> R = new HashMap<>();
//      如果con的值为空或者tm的state属性为假，那么返回map类的r
        if (con == null || !tm.state) return R;
        try {
            ps = con.prepareStatement(sql);//把sql搜索后的数据赋给ps变量
            ps.setInt(1, uid);//preparestatement的第一个参数设置为int类型的uid
            rs = ps.executeQuery();
            while (rs.next()) {
                R.put("uid", rs.getString("uid"));//把rs中uid的值与r这个map中的uid关联；
                R.put("uname", rs.getString("uname"));
                R.put("password", rs.getString("password"));
                R.put("name", rs.getString("name"));
                R.put("group", rs.getString("group"));
                R.put("publishing", Boolean.toString(rs.getBoolean(6)));
                R.put("reply", Boolean.toString(rs.getBoolean(7)));
            }
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            return R;
        } finally {//finally里的代码无论是否异常都会执行
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning("uid版本 - " + e.getMessage());
            }
        }
        return R;
    }

    private Map<String,String> getPerson(String uname){
        String sql = "SELECT * FROM user WHERE user.uname = ? LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, String> R = new HashMap<>();
        if (con == null || !tm.state) return R;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, uname);
            rs = ps.executeQuery();
            while (rs.next()) {
                R.put("uid", rs.getString("uid"));
                R.put("uname", rs.getString("uname"));
                R.put("password", rs.getString("password"));
                R.put("name", rs.getString("name"));
                R.put("group", rs.getString("group"));
                R.put("publishing", Boolean.toString(rs.getBoolean(6)));
                R.put("reply", Boolean.toString(rs.getBoolean(7)));
            }
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            return R;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning("uname版本 - " + e.getMessage());
            }
        }
        return R;
    }

    private Reply getReply(int rid){
        String sql = "SELECT * FROM reply WHERE reply.id = ? LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Reply reply = null;
        if (con == null || !tm.state) return null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, rid);
            rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> ll = getPerson(rs.getInt("uid"));
                reply = new Reply(rs.getInt("id"), rs.getInt("uid"),ll.getOrDefault("uname", "") , ll.getOrDefault("name", ""), rs.getString("context"), rs.getString("notice"), rs.getTimestamp("timestamp").toInstant());
            }
            return reply;
        } catch (Exception e){
            mlog.warning(e.getMessage());
            return reply;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }

    private Notices getNotice(int nid){
        String sql = "SELECT * FROM notices WHERE notices.id = ? LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notices notice = null;
        if (con == null || !tm.state) return null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, nid);
            rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> ll = getPerson(rs.getInt("uid"));
                notice = new Notices(rs.getInt("id"), rs.getInt("uid"),ll.getOrDefault("uname", "") , ll.getOrDefault("name", "NULL"), rs.getString("theme"), rs.getString("content"), rs.getInt("group"), rs.getTimestamp("timestamp").toInstant());
            }
            return notice;
        } catch (Exception e){
            mlog.warning(e.getMessage());
            return notice;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }

    public void deleReply(String token, int rid){
        String sql = "DELETE FROM `reply` WHERE (`id`=?)";
        PreparedStatement ps = null;
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        int uid = Integer.valueOf(tm.uidByToken(token));
        if (uid < 0) throw new RuntimeException("当前服务不可用");
        Reply reply = getReply(rid);
        if (reply == null || reply.getUid() != uid) throw new RuntimeException("您无权删除该回复");
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, rid);
            if (ps.executeUpdate() == 0) throw new RuntimeException("删除失败");
        } catch (Exception e){
            throw new RuntimeException("删除失败");
        } finally {
            try{
                ps.close();
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//  删
    public void deleNotice(String token, int nid){
        String sql = "DELETE FROM `notices` WHERE (`id`=?)";
        PreparedStatement ps = null;
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        int uid = Integer.valueOf(tm.uidByToken(token));
        if (uid < 0) throw new RuntimeException("当前服务不可用");
        Notices notice = getNotice(nid);
        if (notice == null || notice.getUid() != uid) throw new RuntimeException("您无权删除该公告");
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, nid);
            if (ps.executeUpdate() == 0) throw new RuntimeException("删除失败");
        } catch (Exception e){
            throw new RuntimeException("删除失败");
        } finally {
            try{
                ps.close();
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	更新用户
    private static String somesSQL(boolean buname, boolean bname, boolean bgroup, boolean bpublishing, boolean breply, boolean bpass){
        String sql = "UPDATE `user` SET";
        if (buname || bname || bgroup || bpublishing || breply || bpass){
            if (buname){
                if (bname || bgroup || bpublishing || breply || bpass){
                    sql += " `uname`= ? ,";
                } else {
                    sql += " `uname`= ? ";
                }
            }
            if (bname){
                if (bgroup || bpublishing || breply || bpass){
                    sql += " `name`= ? ,";
                } else {
                    sql += " `name`= ? ";
                }
            }
            if (bgroup){
                if (bpublishing || breply || bpass){
                    sql += " `group`= ? ,";
                } else {
                    sql += " `group`= ? ";
                }
            }
            if (bpublishing){
                if (breply || bpass){
                    sql += " `publishing`= ? ,";
                } else {
                    sql += " `publishing`= ? ";
                }
            }
            if (breply){
                if (bpass){
                    sql += " `reply`= ? ,";
                } else {
                    sql += " `reply`= ? ";
                }
            }
            if (bpass){
                sql += " `password`= ? ";
            }
        } else {
            return "";
        }
        return sql + "WHERE (`uid`= ? ) LIMIT 1";
    }
//	修改用户
    public void setUser(String token, String uid, String newuname, String newname, String newpass, String newgroup, String publishing, String reply) throws SQLException {
        String sql = somesSQL(!(newuname == null || newuname.isEmpty()),
                !(newname == null || newname.isEmpty()),
                !(newgroup == null || newgroup.isEmpty()),
                !(publishing == null || publishing.isEmpty()),
                !(reply == null || reply.isEmpty()),
                !(newpass == null || newpass.isEmpty()));
        if (sql.isEmpty()) return;
        PreparedStatement ps = null;
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        if (uid == null) throw new RuntimeException("没有操作权限");
        int muid = Integer.valueOf(uid);
        if (Integer.valueOf(tm.uidByToken(token)) != 1) throw new RuntimeException("没有操作权限");
        try {
            Queue<String>  Q = new LinkedList<>();
            if (!(newuname == null || newuname.isEmpty())) Q.add(newuname);
            if (!(newname == null || newname.isEmpty())) Q.add(newname);
            if (!(newgroup == null || newgroup.isEmpty())) Q.add(newgroup);
            if (!(publishing == null || publishing.isEmpty())) Q.add((publishing.equals("true") ? publishing : "false"));
            if (!(reply == null || reply.isEmpty())) Q.add((reply.equals("true") ? reply : "false"));
            if (!(newpass == null || newpass.isEmpty())){
                String tpass = my.util.EncodeUtil.MSSha1(newpass);
                if (tpass == null ||tpass.isEmpty()) throw new RuntimeException();
                Q.add(tpass);
            }
            ps = con.prepareStatement(sql);
            int i = 1;
            while (!Q.isEmpty()){
                if (Q.peek().equals("true")){
                    Q.poll();
                    ps.setBoolean(i, true);
                } else if (Q.peek().equals("false")){
                    Q.poll();
                    ps.setBoolean(i, false);
                } else {
                    ps.setString(i, Q.poll());
                }
                i++;
            }
            ps.setInt(i, muid);
            if (ps.execute()) throw new RuntimeException();
        } catch (Exception e){
            mlog.warning(e.getMessage());
            throw new RuntimeException("修改失败");
        } finally {
            try {
                ps.close();
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	删除用户
    public void deleUser(String token, ArrayList<Integer> muids) throws Exception{
        String sql = "DELETE FROM `user` WHERE (`uid`=?)";
        PreparedStatement ps = null;
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        int uid = Integer.valueOf(tm.uidByToken(token));
        if (uid != 1) throw new RuntimeException("无权操作");
        try{
            con.setAutoCommit(false);
            ps = con.prepareStatement(sql);
            for (int v : muids) {
                ps.setInt(1, v);
                if (ps.executeUpdate() == 0) throw new RuntimeException();
            }
            con.commit();
        } catch (Exception e){
            con.rollback();
            throw new RuntimeException("删除失败");
        } finally {
            try{
                con.setAutoCommit(true);
                ps.close();
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	增加用户
    public void addUser(String token, String uname, String password, String name, boolean p, boolean r) throws RuntimeException{
        String sql = "INSERT INTO `user` (`uname`, `password`, `name`, `publishing`, `reply`) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (token == null || uname == null || password == null
                || name == null) throw new RuntimeException("当前服务不可用");
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        String uid = tm.uidByToken(token);
        String publishing = tm.publishingByToken(token);
        if (Integer.valueOf(uid) != 1) throw new RuntimeException("无权操作");
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, uname);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setBoolean(4, p);
            ps.setBoolean(5, r);
            if (ps.execute()) throw new RuntimeException();
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            throw new RuntimeException("创建新用户失败");
        } finally {
            try {
                ps.close();
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	查询group
    public String idStringGroup(int id){
        String sql = "SELECT gname FROM `group` WHERE `group`.`gid` = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString("gname");
            }
        } catch (SQLException se){
            mlog.warning(se.getMessage());
            return "";
        }
        return "";
    }
//	提示信息
    public void pushNotice(String token, String theme, String content) throws RuntimeException{
        String sql = "INSERT INTO `notices` (`theme`, `content`, `group`, `uid`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (token == null || theme == null || content == null
                || content.length() > 500) throw new RuntimeException("当前服务不可用");
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        int group = Integer.valueOf(tm.groupByToken(token));
        String uid = tm.uidByToken(token);
        String publishing = tm.publishingByToken(token);
        mlog.info(publishing);
        if (uid == null || group < 0 || publishing == null || publishing.equals("false")) throw new RuntimeException("当前服务不可用");
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, theme);
            ps.setString(2, content);
            ps.setInt(3, group);
            ps.setString(4, uid);
            if (ps.execute()) throw new RuntimeException();
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            throw new RuntimeException("创建新公告失败");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	回复信息
    public void replyNotice(String token, int id, String c) throws RuntimeException{
        String sql = "INSERT INTO `reply` (`uid`, `context`, `notice`) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (con == null || !tm.state) throw new RuntimeException("当前服务不可用");
        int group = Integer.valueOf(tm.groupByToken(token));
        int ngroup = getNoticeGroup(id);
        String uid = tm.uidByToken(token);
        if (group < 0 || ngroup < 0 || uid == null) throw new RuntimeException("当前服务不可用");
//      权限判断
        if (group != ngroup) throw new RuntimeException("无操作权限");
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, uid);
            ps.setString(2, c);
            ps.setInt(3, id);
            if (ps.execute()) throw new RuntimeException();
        } catch (Exception e) {
            mlog.warning(e.getMessage());
            throw new RuntimeException("回复失败");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	获取提示信息的总数？
    public int getNoticeCount (String token){
        String sql = "SELECT count(*) FROM notices WHERE notices.`group` = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (con == null || !tm.state) return 0;
        int group = Integer.valueOf(tm.groupByToken(token));
        if (group < 0) return 0;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, group);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("count(*)");
            }
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
        return 0;
    }
//	获取信息的组别？
    private int getNoticeGroup (int id){
        String sql = "SELECT notices.`group` FROM notices WHERE notices.id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (con == null || !tm.state) return -1;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt("group");
            }
        } catch (Exception e) {
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
        return -1;
    }
//	获取relpy列表
    public List<Reply> getReplys(int notice){
        String sql = "SELECT * FROM reply WHERE reply.notice = ? ORDER BY reply.timestamp DESC";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (con == null || !tm.state) return null;
        try{
            List<Reply> list = new ArrayList<>();
            ps = con.prepareStatement(sql);
            ps.setInt(1, notice);
            rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> ll = getPerson(rs.getInt("uid"));
                Reply reply = new Reply(rs.getInt("id"), rs.getInt("uid"), ll.getOrDefault("uname", ""),ll.getOrDefault("name", ""), rs.getString("context"), rs.getString("notice"), rs.getTimestamp("timestamp").toInstant());
                list.add(reply);
            }
            return list;
        } catch (Exception e){
            mlog.warning(e.getMessage());
            return null;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	获取提示信息
    public List<Notices> getNotices(int start, int offset, String token){
        String sql = "SELECT * FROM notices WHERE notices.`group` = ? ORDER BY notices.timestamp DESC LIMIT ?,?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (con == null || !tm.state) return null;
        int group = Integer.valueOf(tm.groupByToken(token));
        if (group < 0) return null;
        try {
            List<Notices> list = new ArrayList<>();
            ps = con.prepareStatement(sql);
            ps.setInt(1, group);
            ps.setInt(2, start);
            ps.setInt(3, offset);
            rs = ps.executeQuery();
            while (rs.next()){
                Map<String, String> ll = getPerson(rs.getInt("uid"));
                Notices notice = new Notices(rs.getInt("id"), rs.getInt("uid"),ll.getOrDefault("uname", ""), ll.getOrDefault("name", "NULL"), rs.getString("theme"), rs.getString("content"), rs.getInt("group"), rs.getTimestamp("timestamp").toInstant());
                list.add(notice);
            }
            return list;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e){
                mlog.warning(e.getMessage());
            }
        }
    }
//	用户登录的错误信息
    public String authQuery(String uname, String password) throws RuntimeException, SQLException {
        String tpass = my.util.EncodeUtil.MSSha1(password);
        if (con == null || !tm.state) throw new RuntimeException("当前授权服务不可用");
        Map<String, String> lp = getPerson(uname);
        if (lp.isEmpty()) throw new RuntimeException("用户不存在");
        if (lp.get("password").equals(tpass)){
            return tm.createToken(uname, lp);
        } else {
            throw new RuntimeException("用户名/密码错误");
        }
    }
}
