package com.bittech.everything.core.model;

import lombok.Data;

@Data  //生成getter setter toString

public class Thing {
    //文件名称
    private String name;

    //文件路径
    private String  path;

    //文件路径深度
    private Integer depth;

    //文件类型
    private  FileType fileType;
}

