package com.lu.fingerprint;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @Author: luqihua
 * @Time: 2018/6/29
 * @Description: SharePereferenceTool
 */
public class SharePreferenceTool {
    private final String sp_name = "com.lu.fingerprint";
    private SharedPreferences sharedPreferences;


    private static class Holder {
        private static SharePreferenceTool sInstance = new SharePreferenceTool();
    }

    public static SharePreferenceTool getInstance() {
        return Holder.sInstance;
    }

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(sp_name, Context.MODE_PRIVATE);
    }

    public void saveObject(String key, Serializable serializable) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos);
            objectOutputStream.writeObject(serializable);
            String objectStr = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            objectOutputStream.close();
            sharedPreferences.edit().putString(key, objectStr).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T getObject(String key) {
        try {
            final String objectStr = sharedPreferences.getString(key, "");
            if (objectStr.length() == 0) return null;
            byte[] bytes = Base64.decode(objectStr, Base64.DEFAULT);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(bis);
            return (T) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void saveStr(String key, String str) {
        sharedPreferences.edit().putString(key, str).apply();
    }

    public String getStr(String key) {
        return sharedPreferences.getString(key, "");
    }


    public void saveFingerData(String key, byte[] textArray, byte[] initializeVector) {
        final String textStr = Base64.encodeToString(textArray, Base64.DEFAULT);
        final String ivStr = Base64.encodeToString(initializeVector, Base64.DEFAULT);
        sharedPreferences.edit().putString(key, textStr + "," + ivStr).apply();
    }

    public byte[][] getFingerData(String key) {
        final String dataStr = sharedPreferences.getString(key, null);
        if (dataStr == null) return null;
        String[] datas = dataStr.split(",");
        if (datas.length != 2) return null;

        byte[] textArray = Base64.decode(datas[0], Base64.DEFAULT);
        byte[] initializeVector = Base64.decode(datas[1], Base64.DEFAULT);

        return new byte[][]{textArray, initializeVector};
    }

}
