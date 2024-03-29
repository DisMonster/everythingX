package com.bittech.everything.core.search;

import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;

public interface FileSearch {

        /**
         * 根据condition条件进行数据库的检索
         */
        List<Thing> search(Condition condition);

}