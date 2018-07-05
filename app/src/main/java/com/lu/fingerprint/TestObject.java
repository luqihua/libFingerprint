package com.lu.fingerprint;

import java.io.Serializable;

/**
 * @Author: luqihua
 * @Time: 2018/6/29
 * @Description: TestObject
 */
public class TestObject extends Object implements Serializable,Cloneable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -3245478690496182643L;

    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
