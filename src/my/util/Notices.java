package my.util;

import java.time.Instant;

public class Notices {
    private int id;
    private int uid;
    private String uname;
    private String name;
    private String theme;
    private String content;
    private int group;
    private Instant timestamp;

    public Notices(int id, int uid, String uname, String name, String theme, String content, int group, Instant timestamp) {
        this.id = id;
        this.uid = uid;
        this.uname = uname;
        this.name = name;
        this.theme = theme;
        this.content = content;
        this.group = group;
        this.timestamp = timestamp;
    }
//获取id
    public int getId() {
        return id;
    }
//获取uid
    public int getUid() {
        return uid;
    }
//获取uname
    public String getUname() {
        return uname;
    }
//获取姓名
    public String getName() {
        return name;
    }
//获取主题
    public String getTheme() {
        return theme;
    }
//获取内容
    public String getContent() {
        return content;
    }
//获取组别
    public int getGroup() {
        return group;
    }
//获取时间戳
    public Instant getTimestamp() {
        return timestamp;
    }
}
