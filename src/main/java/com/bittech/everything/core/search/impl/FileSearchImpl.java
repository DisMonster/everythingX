package com.bittech.everything.core.search.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

public class FileSearchImpl implements FileSearch {

    private final FileIndexDao fileIndexDao;

    /**
     *被final修饰的变量初始化有三种方法
     * 1.直接初始化
     * 2.构造方法初始化
     * 3.构造块中初始化
     */


    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        if (condition == null) {
            return new ArrayList<>();
        }
        return this.fileIndexDao.search(condition);
    }
}