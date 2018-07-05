package com.lu.fingerprint;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("fingerprint.lu.com.fingerprintdemo", appContext.getPackageName());
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

}
