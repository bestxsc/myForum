package my.util;

import java.time.Instant;

public class Reply {
    private int id;
    private int uid;
    private String uname;
    private String name;
    private String context;
    private String notice;
    private Instant timestamp;

    public Reply(int id, int uid, String uname, String name, String context, String notice, Instant timestamp) {
        this.id = id;
        this.uid = uid;
        this.uname = uname;
        this.name = name;
        this.context = context;
        this.notice = notice;
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

    public String getContext() {
        return context;
    }

    public String getNotice() {
        return notice;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
