package com.dd.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {
    private static MD5 _instance = new MD5();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private MessageDigest messageDigest;

    private MD5() {
        try {
            this.messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var2) {
            this.log.error("Could not instantiate the MD5 Message Digest!");
        }
    }

    public static MD5 getInstance() {
        return _instance;
    }

    public synchronized String getHash(byte[] data) {
        this.messageDigest.update(data);
        return toHexString(this.messageDigest.digest());
    }

    private String toHexString(byte[] byteData) {
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(byteData[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }

            sb.append(hex);
        }

        return sb.toString();
    }
}