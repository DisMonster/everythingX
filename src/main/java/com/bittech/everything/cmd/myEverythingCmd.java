package com.bittech.everything.cmd;

import com.bittech.everything.core.myEverythingManager;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.config.myEverythingConfig;

import java.util.List;
import java.util.Scanner;

public class myEverythingCmd {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //解析参数
        parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        myEverythingManager manager = myEverythingManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //启动监控
        //manager.startFileSystemMonitor();

        //交互式
        interactive(manager);

    }

    private static void parseParams(String[] args){
        myEverythingConfig config = myEverythingConfig.getInstance();
        for(String param : args){
            //
            String maxReturnParam = "--maxReturn=";
            if(param.startsWith(maxReturnParam)){
                //maxReturn = value
                int index = param.indexOf("=");
                String maxReturnStr = param.substring(index+1);
                try{
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturn(maxReturn);
                }catch (NumberFormatException e){
                    //如果输入格式不对，采用默认值即可。
                }
            }
            //
            String deptOrderAscParam = "--deptOrderAsc=";
            if(param.startsWith(deptOrderAscParam)){
                int index = param.indexOf("=");
                String deptOrderAscStr= param.substring(index+1);
                config.setDeptOrderAsc(Boolean.parseBoolean(deptOrderAscStr));
            }
            //
            String includePathParam = "--includePath=";
            if(param.startsWith(includePathParam)){
                int index = param.indexOf("=");
                String includePathStr= param.substring(index+1);
                String[] includePaths = includePathStr.split(";");
                if(includePaths.length>0){
                    config.getIncludePath().clear();
                }
                for(String p : includePaths){
                    config.getIncludePath().add(p);
                }
            }
            //bug
            String excludePathParam = "--excludePath=";
            if(param.startsWith(excludePathParam)){
                int index = param.indexOf("=");
                String excludePathStr= param.substring(index+1);
                String[] excludePaths = excludePathStr.split(";");
                config.getExcludePath().clear();
                for(String p : excludePaths){
                    config.getExcludePath().add(p);
                }
            }
        }
    }


    private static void interactive(myEverythingManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        System.out.println("请输入正确命令格式");
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    System.out.println("请输入正确命令格式");
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                case "set":
                    set();
                    break;
                default:
                    System.out.println("输入错误，请输入正确指令");
                    help();
            }
        }
    }
    private static void search(myEverythingManager manager, Condition condition) {
        //name fileType limit orderByAsc
        condition.setLimit(myEverythingConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(myEverythingConfig.getInstance().getDeptOrderAsc());
        long startTime = System.currentTimeMillis();
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("搜索用时："+(endTime-startTime)+" ms");
    }

    private static void index(myEverythingManager manager) {
        //`::`  关键字来访问类的方法。
        //统一调度器中的index
        //new Thread(manager::buildIndex).start();
        manager.buildIndex();
    }

    private static void quit() {
        System.out.println("再见");

        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，Everything ");
        help();
        System.out.println("如果您是第一次使用或者进行了设置，请先索引（输入index），此过程需要消耗一定时间，请耐心等待。");
        System.out.println("注意：在进行搜索时，file-name(文件名)必须输入,file-type(文件类型)可选");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("设置：set");
        System.out.println("搜索：search file-name.file-type");
    }

    public static void set(){
        System.out.println("参数列表如下：");
        System.out.println("maxReturn:搜索后显示结果的个数，默认输出全部结果");
        System.out.println("includePath:检索的路径，默认电脑的所有磁盘");
        System.out.println("deptOrderAsc:文件深度的排序方式，默认升序（路径深的文件在最先输出）");
        while(true){
            System.out.println("请输入要设置的属性名称,退出输入quit");
            String input = scanner.nextLine();
            switch (input){
                case "maxReturn":
                    setmaxReturn();
                    break;
                case "includePath":
                    setincludePath();
                    break;
                case "deptOrderAsc":
                    setdeptOrderAsc();
                    break;
                case "quit":
                    return;
                default:
                    System.out.println("输入错误");
            }
        }
    }

    private static void setmaxReturn(){
        myEverythingConfig config = myEverythingConfig.getInstance();
        System.out.println("请输入显示的结果个数");
        String maxReturn = scanner.nextLine();
        if(maxReturn.equals("")){
            return;
        }
        try{
            config.setMaxReturn(Integer.parseInt(maxReturn));
        }catch (NumberFormatException e){
            //如果输入格式不对，采用默认值即可。
        }
        System.out.println("设置完成");
    }

    private static void setincludePath(){
        myEverythingConfig config = myEverythingConfig.getInstance();
        System.out.println("请输入要检索的路径");
        String includePath = scanner.nextLine();
        if(includePath.equals("") || includePath.contains(" ")){
            System.out.println("输入错误");
            return;
        }
        String[] includePaths = includePath.split(";");
        if(includePaths.length>0){
            config.getIncludePath().clear();
        }
        for(String p : includePaths){
            config.getIncludePath().add(p);
        }
        System.out.println("设置完成，请先输入index后再进行search");
    }

    private static void setdeptOrderAsc(){
        myEverythingConfig config = myEverythingConfig.getInstance();
        System.out.println("请输入排序方式，升序：true   降序：false");
        String deptOrderAsc = scanner.nextLine();
        if(deptOrderAsc!="true" || deptOrderAsc!="false"){
            System.out.println("输入错误");
            return;
        }
        config.setDeptOrderAsc(Boolean.parseBoolean(deptOrderAsc));
        System.out.println("设置完成");
    }

}
