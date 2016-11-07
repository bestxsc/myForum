package my.util;

import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.logging.Logger;

public class TokenManager {
    public boolean state = true;//缺省值为真
    private static final int EXPIRATION_TIME = 600;//等待时间最大为600s
    private static final String KEY = "sorry,man!";
    private static final String REDIS_URL = "localhost";//redis的url
    private Jedis jedis = null;//?连接池？
    private static Logger mlog;//日志记录器

    static {
        mlog = Logger.getLogger("my.util.TokenManager");
    }

    public TokenManager(){
        try {
            jedis = new Jedis(REDIS_URL);
            jedis.ping();//?
        } catch (Exception e) {
            mlog.warning("缓存连接失败：" + e.getMessage());
            this.state = false;
        }
    }

    public String createToken(String uname, Map<String, String> p){
        if (uname == null) return "";
        long tt = System.currentTimeMillis() / 1000;
        String htoken = jedis.get(uname);//获取uname中的值
        //如果uname不为空，且htoken键存在，且uname字段中的htoken和unmade相同，那么返回htoken中的值
        if (htoken != null && jedis.exists(htoken) && jedis.hget(htoken, "uname").equals(uname)) {
            return htoken;
        } else {
        	//htoken用md5算法加密后在用sha1算法加密
            htoken = EncodeUtil.SHA1(EncodeUtil.MD5(uname + Long.toString(tt) + KEY));
            //hmset方法用新的htoken值替代旧的
            jedis.hmset(htoken, p);
            //uname字符串中的值替换成htoken字符串中的值
            jedis.set(uname, htoken);
            //？？
            jedis.expireAt(htoken, tt + EXPIRATION_TIME);
            jedis.expireAt(uname, tt + EXPIRATION_TIME);
            return htoken;
        }
    }
    //验证
    public boolean verificateToken(String token, String uname){
        long tt = System.currentTimeMillis() / 1000;
        if (token == null || uname == null) return false;
        if (jedis.exists(token) && jedis.hget(token, "uname").equals(uname) && jedis.exists(uname) && jedis.get(uname).equals(token)){
            jedis.expireAt(token, tt + EXPIRATION_TIME);
            jedis.expireAt(uname, tt + EXPIRATION_TIME);
            return true;
        }
        return false;
    }

    public String nameByToken(String token){
        if (token == null) return null;
        if (jedis.exists(token) && jedis.hexists(token, "name")){
            return jedis.hget(token, "name");
        }
        return null;
    }

    public String uidByToken(String token){
        if (token == null) return null;
        if (jedis.exists(token) && jedis.hexists(token, "uid")){
            return jedis.hget(token, "uid");
        }
        return null;
    }

    public String groupByToken(String token){
        if (token == null) return null;
        if (jedis.exists(token) && jedis.hexists(token, "group")){
            return jedis.hget(token, "group");
        }
        return null;
    }

    public String publishingByToken(String token){
        if (token == null) return null;
        //token键中的publishing字段哈希值是否存在
        if (jedis.exists(token) && jedis.hexists(token, "publishing")){
            //返回token键中publishing的时间复杂度--->？？？
        	return jedis.hget(token, "publishing");
        }
        return null;
    }

    public String replyByToken(String token){
        if (token == null) return null;
        if (jedis.exists(token) && jedis.hexists(token, "reply")){
            return jedis.hget(token, "reply");
        }
        return null;
    }
}
