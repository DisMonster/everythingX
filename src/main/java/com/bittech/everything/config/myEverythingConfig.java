package com.bittech.everything.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class myEverythingConfig {

    private static volatile myEverythingConfig config;

    /**
     * 建立索引的路径
     */
    @Setter
    private Set<String> includePath = new HashSet<>();
    /**
     * 排除索引文件的路径
     */
    @Setter
    private Set<String> excludePath = new HashSet<>();


    //TODO 可配置的参数会在这里体现
    //检索最大的返回值数量
    @Setter
    private Integer maxReturn = 30;

    //深度排序的规则，默认是升序
    //order by dept asc limit 30 offset 0
    @Setter
    private Boolean deptOrderAsc = true;

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "my_everything";

    private myEverythingConfig() {

    }

    private void initDefaultPathsConfig() {
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));
        //排除的目录
        //windows ： C:\Windows C:\Program Files (x86) C:\Program Files  C:\ProgramData
        //linux : /tmp /etc
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")) {
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");

        } else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }

    public static myEverythingConfig getInstance() {
        if (config == null) {
            synchronized(myEverythingConfig.class) {
                if (config == null) {
                    config = new myEverythingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }
}
