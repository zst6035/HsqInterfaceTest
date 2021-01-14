package com.hsq.model;

import lombok.Data;

@Data
public class Merchant {
    private int id;
    private String merchantNo;
    private String privateKeyPwd;
    private String key;
    private String RsaKey;
    private String PrivateKeyPath;

}
