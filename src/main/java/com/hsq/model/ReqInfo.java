package com.hsq.model;

import lombok.Data;

@Data
public class ReqInfo {
    private int id;
    private String method;
    private String signContent;
    private String url;
    private String description;

    @Override
    public String toString() {
        return "ReqInfo{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", signContent='" + signContent + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + description + '\'' +
                '}';
    }
}
