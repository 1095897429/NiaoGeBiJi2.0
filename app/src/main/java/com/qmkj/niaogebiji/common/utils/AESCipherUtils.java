package com.qmkj.niaogebiji.common.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:AES加密
 */
public class AESCipherUtils {

    //private static final String CipherMode = "AES/ECB/PKCS5Padding";使用ECB加密，不需要设置IV，但是不安全
    private static final String CipherMode = "AES/CFB/NoPadding";//使用CFB加密，需要设置IV


    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    //密钥
    byte[] bKey = new byte[]{0x30, 0x31, 0x31, 0x32, 0x67, 0x5A, 0x68, 0x47, 0x47, 0x47, 0x41, 0x35, 0x20, 0x20, 0x20, 0x20, 0x20,
            0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
    //密钥向量
    byte[] bVector = new byte[]{0x31, 0x30, 0x30, 0x33, 0x63, 0x65, 0x20, 0x20, 0x20, 0x21, 0x21, 0x20, 0x20, 0x20, 0x20, 0x20};

    /**
     * 加密
     * @param srcData 原数据
     * @param key 密钥
     * @param iv 密钥向量
     * @return
     */
    public static byte[] AES_cbc_encrypt(byte[] srcData, byte[] key, byte[] iv) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encData = cipher.doFinal(srcData);
            return encData;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;

    }


    /**
     * 解密
     * @param encData 需解密数据
     * @param key 密钥
     * @param iv 密钥向量
     * @return
     */
    public static String AES_cbc_decrypt(byte[] encData, byte[] key, byte[] iv) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            return new String(cipher.doFinal(encData), Charset.forName("UTF8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }















    /**
     * 对字符串加密
     *
     * @param key  密钥
     * @param data 源字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String key, String data) {
        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 加密(结果为16进制字符串) **/
    public static String encrypt(String content, String secretKey, String iv) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, secretKey, iv);
        String result = Base64.encodeToString(data,Base64.DEFAULT);
        return result;
    }

    /** 加密字节数据 **/
    public static byte[] encrypt(byte[] content, String secretKey, String iv) {
        try {
            SecretKeySpec key = createKey(secretKey);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, createIV(iv));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对字符串解密
     *
     * @param key  密钥
     * @param data 已被加密的字符串
     * @return 解密得到的字符串
     */
    public static String decrypt(String key, String data)  {
        try {
            byte[] encrypted1 = Base64.decode(data.getBytes(), Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keyspec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    /** 创建密钥 **/
    private static SecretKeySpec createKey(String key) {
        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }


        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    /** 创建偏移量 **/
    private static IvParameterSpec createIV(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(password);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new IvParameterSpec(data);
    }






}
