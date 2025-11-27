package com.yzb.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileCopy {
    public static void main(String[] args) throws IOException {
       String source = "D:\\from";
       String target = "D:\\to";
        Files.walk(Paths.get(source)).forEach(path -> {
            String targetPath = path.toString().replace(source, target);
            // 判断是文件还是目录
            if (Files.isDirectory(path)) {
                try {
                    Files.createDirectory(Paths.get(targetPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (Files.isRegularFile(path)) {
                try {
                    Files.copy(path, Paths.get(targetPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
