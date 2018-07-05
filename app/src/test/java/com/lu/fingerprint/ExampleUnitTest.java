package com.lu.fingerprint;

import android.security.keystore.KeyProperties;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES
//            + "/" + KeyProperties.BLOCK_MODE_CBC
//            + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES;


    @Test
    public void addition_isCorrect() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
        keyGenerator.init(new SecureRandom("123456".getBytes()));
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        cipher.update("helloworld".getBytes());
        cipher.update("aaa".getBytes());
        byte[] bytes = cipher.doFinal();
        System.out.println("加密的数据： " + Base64.getEncoder().encodeToString(bytes));

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
         messageDigest = (MessageDigest) messageDigest.clone();
//        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        bytes = messageDigest.digest("helloworld".getBytes());
        System.out.println("SHA: " + toHexString(bytes));

    }

    private static String toHexString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        int len = data.length;
        String hex;
        for (int i = 0; i < len; i++) {
            hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                builder.append("0");
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    @Test
    public void testClone() throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec("123456".getBytes(),"HmacMD5");
        Mac mac =Mac.getInstance("HmacMD5");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal("helloworld".getBytes());
//        System.out.println(Base64.getEncoder().encodeToString(bytes));
        System.out.println(toHexString(bytes));



        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] digest = messageDigest.digest(("123456" + "helloworld").getBytes());
        System.out.println(toHexString(digest));
    }
}