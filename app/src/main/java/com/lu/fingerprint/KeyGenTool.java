package com.lu.fingerprint;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * @Author: luqihua
 * @Time: 2018/6/29
 * @Description: KeyGenTool
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class KeyGenTool {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String KEY_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String KEY_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KEY_ALGORITHM + "/" + KEY_BLOCK_MODE + "/" + KEY_PADDING;
    private final String KEY_ALIAS;

    public KeyGenTool(Context context) {
        KEY_ALIAS = context.getPackageName();
    }

    public Cipher getEncryptCipher() {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    /**
     * 获取解密的cipher
     *
     * @param parameterSpec 加密cipher的一些参数
     *                      包括initialize vector(AES加密中 以CBC模式加密需要一个初始的数据块，解密时同样需要这个初始块)
     * @return
     */
    public Cipher getDecryptCipher(AlgorithmParameterSpec parameterSpec) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
            //解密的cipher需要使用加密的cipher的参数encryptCipher.getParameters()，否则在解密的时候会报错
            cipher.init(Cipher.DECRYPT_MODE, getKey(), parameterSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    private SecretKey getKey() throws Exception {
        SecretKey secretKey = null;
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        if (keyStore.isKeyEntry(KEY_ALIAS)) {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            secretKey = secretKeyEntry.getSecretKey();
        } else {
            secretKey = createKey();
        }

        return secretKey;
    }


    /**
     * 在Android中，key的创建之后必须存储在秘钥库才能使用
     * @return
     * @throws Exception
     */
    private SecretKey createKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, ANDROID_KEY_STORE);
        KeyGenParameterSpec spec = new KeyGenParameterSpec
                .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KEY_BLOCK_MODE)
                .setEncryptionPaddings(KEY_PADDING)
                .setUserAuthenticationRequired(true)
                .build();
        keyGenerator.init(spec);
        return keyGenerator.generateKey();
    }

}
