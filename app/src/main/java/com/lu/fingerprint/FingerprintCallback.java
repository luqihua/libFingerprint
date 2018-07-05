package com.lu.fingerprint;

/**
 * @Author: luqihua
 * @Time: 2018/6/30
 * @Description: FingerprintCallback
 */
public interface FingerprintCallback {
    void success(FingerprintTool.Mode mode,String result,String ivStr);
    void error(Exception e);
}
