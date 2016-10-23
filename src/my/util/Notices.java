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

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getName() {
        return name;
    }

    public String getTheme() {
        return theme;
    }

    public String getContent() {
        return content;
    }

    public int getGroup() {
        return group;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
