package com.lu.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

/**
 * @Author: luqihua
 * @Time: 2018/6/29
 * @Description: FingerprintTool
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintTool extends FingerprintManagerCompat.AuthenticationCallback {
    enum Mode {
        ENCRYPT, DECRYPT
    }

    private Mode mode;

    private Context mContext;
    private KeyGenTool mKeyGenTool;
    private FingerprintManagerCompat mFingerprintManager;
    private CancellationSignal mCancellationSignal;

    private String mEncryptText;//待解密的文本
    private String mOriginalText;//待加密文本

    private FingerprintCallback mCallback;

    public FingerprintTool(Context context) {
        this.mContext = context;
        mFingerprintManager = FingerprintManagerCompat.from(context);
        mKeyGenTool = new KeyGenTool(context);
        mCancellationSignal = new CancellationSignal();
    }

    public void setCallback(FingerprintCallback callback) {
        this.mCallback = callback;
    }

    public void encryptText(String originalText) {
        if (!checkAvailable()) return;
        this.mode = Mode.ENCRYPT;
        this.mOriginalText = originalText;
        Cipher cipher = mKeyGenTool.getEncryptCipher();
        mFingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(cipher), 0,
                mCancellationSignal,
                this, null);
    }


    public void decryptText(String encryptText, String ivStr) {
        if (!checkAvailable()) return;
        this.mode = Mode.DECRYPT;
        this.mEncryptText = encryptText;
        Cipher cipher = mKeyGenTool.getDecryptCipher(new IvParameterSpec(base64Decode(ivStr)));
        mFingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(cipher), 0,
                mCancellationSignal,
                this, null);
    }

    public void cancel() {
        if (!mCancellationSignal.isCanceled())
            mCancellationSignal.cancel();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        if (result == null
                || result.getCryptoObject() == null
                || result.getCryptoObject().getCipher() == null) {
            mCallback.error(new Exception("CryptoObject is null"));
            return;
        }
        Cipher cipher = result.getCryptoObject().getCipher();
        try {
            if (mode == Mode.ENCRYPT) {
                byte[] bytes = cipher.doFinal(mOriginalText.getBytes());
                byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
                final String ivStr = base64Encode(iv);
                final String text = base64Encode(bytes);
                mCallback.success(mode, text, ivStr);
            } else if (mode == Mode.DECRYPT) {
                byte[] bytes = base64Decode(mEncryptText);
                final String text = new String(cipher.doFinal(bytes));
                mCallback.success(mode, text, null);
            }
        } catch (Exception e) {
            mCallback.error(e);
        }
    }

    @Override
    public void onAuthenticationFailed() {
        mCallback.error(new Exception("指纹不匹配"));
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
        mCallback.error(new Exception(errString.toString()));
    }


    private boolean checkAvailable() {
        //检查当前系统是否处于安全保护中
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || !mFingerprintManager.isHardwareDetected()) {
            mCallback.error(new Exception("当前手机不支持指纹识别"));
            return false;
        } else if (!keyguardManager.isKeyguardSecure()) {
            mCallback.error(new Exception("手机未处于安全保护状态下，请设置安全密码"));
            return false;
        } else if (!mFingerprintManager.hasEnrolledFingerprints()) {
            //检查用户是否录入了指纹
            mCallback.error(new Exception("当前系统未录入指纹"));
            return false;
        }
        return true;
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static byte[] base64Decode(String text) {
        return Base64.decode(text, Base64.URL_SAFE | Base64.NO_WRAP);
    }
}
