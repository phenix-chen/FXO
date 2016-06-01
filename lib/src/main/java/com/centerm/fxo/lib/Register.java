package com.centerm.fxo.lib;

/**
 * Created by administrator on 14-4-24.
 */
public class Register {
    public String address;
    public String value;

    public Register(String receiveMsg) {
        if (receiveMsg != null && receiveMsg.length() >= 5 && receiveMsg.contains(":")) {
            String[] tmps = receiveMsg.split(":");
            if (tmps.length == 2) {
                address = tmps[0];
                value = tmps[1];
            }
        }
    }
}
