//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadLocal {
    public UploadLocal() {
    }

    public static void listFileNames(String positiveImage, String oppositeImage, String handImage, InputStream in1, InputStream in2, InputStream in3, String path) {
        FileOutputStream os = null;

        try {
            // 保存到临时文件1K的数据缓冲
            byte[] bs = new byte[1024];
            // 输出的文件流保存到本地文件
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            os = new FileOutputStream(tempFile.getPath() + File.separator + positiveImage);

            // 读取到的数据长度
            int len;
            // 开始读取
            while((len = in1.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

            os = new FileOutputStream(tempFile.getPath() + File.separator + oppositeImage);

            // 开始读取
            while((len = in2.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

            os = new FileOutputStream(tempFile.getPath() + File.separator + handImage);

            // 开始读取
            while((len = in3.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            System.out.println("UploadLocal.listFileNames, succeed");
        } catch (IOException var21) {
            var21.printStackTrace();
            System.out.println("UploadLocal.listFileNames, error occurred(IOException): " + var21);
        } catch (Exception var22) {
            var22.printStackTrace();
            System.out.println("UploadLocal.listFileNames, error occurred(Exception): " + var22);
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                in1.close();
                in2.close();
                in3.close();
            } catch (IOException var20) {
                var20.printStackTrace();
            }

        }

    }

    public static void listFileNames(String fileName, InputStream inputStream, String path) {
        FileOutputStream os = null;

        try {
            // 保存到临时文件1K的数据缓冲
            byte[] bs = new byte[1024];
            // 输出的文件流保存到本地文件
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);

            // 读取到的数据长度
            int len;
            // 开始读取
            while((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException var17) {
            var17.printStackTrace();
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException var16) {
                var16.printStackTrace();
            }

        }

    }
}
