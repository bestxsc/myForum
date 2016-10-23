package my.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeUtil {
    private final static String key = "hello,man!";

    public static void main(String[] args) {
        System.out.println(Encrypt("你好", "0000000000000000"));
        System.out.println(Decrypt(Encrypt("你好", "0000000000000000"), "0000000000000000"));
        System.out.println(MSSha1("123"));
    }

    public static String MSSha1(String input){
        if (!input.equals("")){
            return SHA1(MD5(input) + key);
        } else {
            return input;
        }
    }

    protected static String MD5(String input) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(input.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMd : md) {
                String shaHex = Integer.toHexString(aMd & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 加密
    private static String Encrypt(String sSrc, String sKey) {
        try {
            if (sKey == null) {
                Logger.getLogger("my.util.EncodeUtil").warning("Key为空null");
                return "";
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                Logger.getLogger("my.util.EncodeUtil").warning("Key长度不是16位");
                return "";
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            Logger.getLogger("my.util.EncodeUtil").warning(e.getMessage());
            return "";
        }
    }

    // 解密
    private static String Decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                Logger.getLogger("my.util.EncodeUtil").warning("Key为空null");
                return "";
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                Logger.getLogger("my.util.EncodeUtil").warning("Key长度不是16位");
                return "";
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                return new String(original,"utf-8");
            } catch (Exception e) {
                Logger.getLogger("my.util.EncodeUtil").warning(e.toString());
                return "";
            }
        } catch (Exception ex) {
            Logger.getLogger("my.util.EncodeUtil").warning(ex.toString());
            return "";
        }
    }
}
