//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.upload.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UploadImage {
    public static final String host = "192.168.1.203";
    public static final int port = 22;
    public static final String username = "upload";
    public static final String password = "12lianupload";

    public UploadImage() {
    }

    public static List<String> listFileNames(String positiveImage, String oppositeImage, String handImage, InputStream in1, InputStream in2, InputStream in3, String username, String password, String host, Integer port, String path) {
        List<String> list = new ArrayList();
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;

        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            sftp = (ChannelSftp)sshSession.openChannel("sftp");
            sftp.connect();
            sftp.cd(path);
            sftp.put(in1, positiveImage);
            sftp.put(in2, oppositeImage);
            sftp.put(in3, handImage);
        } catch (Exception var20) {
            var20.printStackTrace();
        } finally {
            closeChannel(sftp);
            closeChannel((Channel)channel);
            closeSession(sshSession);
        }

        return list;
    }

    public static void closeChannel(Channel channel) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }

    }

    public static void closeSession(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }

    }

    public static List<String> listFileNames(String positiveImage, InputStream in, String username, String password, String host, Integer port, String path) {
        List<String> list = new ArrayList();
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;

        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            sftp = (ChannelSftp)sshSession.openChannel("sftp");
            sftp.connect();
            sftp.cd(path);
            sftp.put(in, positiveImage);
        } catch (Exception var16) {
            var16.printStackTrace();
        } finally {
            closeChannel(sftp);
            closeChannel((Channel)channel);
            closeSession(sshSession);
        }

        return list;
    }
}
