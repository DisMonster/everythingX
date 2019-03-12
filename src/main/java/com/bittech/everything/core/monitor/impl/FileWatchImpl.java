package com.bittech.everything.core.monitor.impl;

import com.bittech.everything.core.common.FileConvertThing;
import com.bittech.everything.core.common.HandlePath;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

//在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
//由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
//如果有文件变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）


public class FileWatchImpl implements FileWatch,FileAlterationListener {
    private FileIndexDao fileIndexDao;
    private FileAlterationMonitor monitor;

    public FileWatchImpl(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10);
    }
    @Override
    public void onStart(FileAlterationObserver observer) {
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate "+directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryChange " + directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete "+directory);
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("onFileCreate "+file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange " +file);
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("onFileDelete "+file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
    }
    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void monitor(HandlePath handlePath) {
        //监控的是includePath集合
        for(String path:handlePath.getIncludePath()){
            FileAlterationObserver observer = new FileAlterationObserver(path,pathname -> {
                String currentPath = pathname.getAbsolutePath();
                for(String excludePath:handlePath.getExcludePath()){
                    if(excludePath.startsWith(currentPath)){
                        return false;
                    }
                }
                return true;
            });
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }
    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
