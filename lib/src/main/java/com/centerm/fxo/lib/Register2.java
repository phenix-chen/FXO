package com.centerm.fxo.lib;

/**
 * Created by Chen on 16/5/30.
 */
public class Register2 {
    public String address;
    public String value;

    public Register2(String receiveMsg) {
        if (receiveMsg != null && receiveMsg.length() >= 3 && receiveMsg.contains(":")) {
            String[] tmps = receiveMsg.split(":");
            if (tmps.length == 2) {
                address = tmps[0];
                value = tmps[1];
            }
        }
    }
}
