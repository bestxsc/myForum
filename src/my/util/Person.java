package my.util;

public class Person {
    private int uid;
    private String uname;
    private String name;
    private int group;
    private boolean publishing;
    private boolean reply;

    public Person(int uid, String uname, String name, int group, boolean publishing, boolean reply) {
        this.uid = uid;
        this.uname = uname;
        this.name = name;
        this.group = group;
        this.publishing = publishing;
        this.reply = reply;
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

    public int getGroup() {
        return group;
    }

    public boolean isPublishing() {
        return publishing;
    }

    public boolean isReply() {
        return reply;
    }
}
