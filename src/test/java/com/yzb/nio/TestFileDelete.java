package com.yzb.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TestFileDelete {
    public static void main(String[] args) throws IOException {
        // 删除文件
        Files.walkFileTree(Paths.get("D:\\Study"), new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("==>进入目录：" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("访问文件：" + file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException ex) throws IOException {
                System.out.println("<==退出目录：" + dir);
                return super.postVisitDirectory(dir, ex);
            }
        });
    }
}
