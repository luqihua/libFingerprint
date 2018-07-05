package com.lu.fingerprint;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.lu.swirl.SwirlView;

/**
 * @Author: luqihua
 * @Time: 2018/7/4
 * @Description: FingerprintDialog
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintDialog extends Dialog implements FingerprintCallback {

    private Context mContext;
    private SwirlView mSwirlView;
    private FingerprintTool mFingerprintTool;

    public FingerprintDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fingerprint);
        init();
    }

    private void init() {
        mFingerprintTool = new FingerprintTool(mContext);
        mFingerprintTool.setCallback(this);
        mSwirlView = findViewById(R.id.swirl_view);
    }

    @Override
    public void show() {
        super.show();

    }

    public void encrypt() {
        show();
        mSwirlView.setState(SwirlView.State.ON);
        mFingerprintTool.encryptText("helloworld");
    }


    public void decrypt() {
        show();
        mSwirlView.setState(SwirlView.State.ON);
        final String text = SharePreferenceTool.getInstance().getStr("encryptText");
        final String iv = SharePreferenceTool.getInstance().getStr("encryptIv");
        mFingerprintTool.decryptText(text, iv);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void success(FingerprintTool.Mode mode, String result, String ivStr) {
        mSwirlView.setState(SwirlView.State.SUCCESS);
        if (mode == FingerprintTool.Mode.ENCRYPT) {
            Log.d("FingerprintDialog", "加密数据： " + result);
            SharePreferenceTool.getInstance().saveStr("encryptText", result);
            SharePreferenceTool.getInstance().saveStr("encryptIv", ivStr);
            Toast.makeText(mContext, "加密成功", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("FingerprintDialog", "解密数据： " + result);
        }
    }

    @Override
    public void error(Exception e) {
        mSwirlView.setState(SwirlView.State.ERROR);
        e.printStackTrace();
    }
}
