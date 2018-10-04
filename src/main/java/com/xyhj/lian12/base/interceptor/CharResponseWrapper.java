//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.base.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CharResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream buffer = null;
    private ServletOutputStream out = null;
    private PrintWriter writer = null;

    public CharResponseWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
        resp.setCharacterEncoding("utf-8");
        this.buffer = new ByteArrayOutputStream();// 真正存储数据的流
        this.out = new CharResponseWrapper.WapperedOutputStream(this.buffer);
        this.writer = new PrintWriter(new OutputStreamWriter(this.buffer, "utf-8"));
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return this.out;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException {
        return this.writer;
    }

    public void flushBuffer() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }

        if (this.writer != null) {
            this.writer.flush();
        }

    }

    public void reset() {
        this.buffer.reset();
    }

    public String getContent() throws IOException {
        // 将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据
        this.flushBuffer();
        return new String(this.buffer.toByteArray(), "utf8");
    }

    // 内部类，对ServletOutputStream进行包装
    private class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            this.bos = stream;
        }

        public void write(int b) throws IOException {
            this.bos.write(b);
        }

        public boolean isReady() {
            return false;
        }

        public byte[] toByteArray() {
            return this.bos.toByteArray();
        }

        public void setWriteListener(WriteListener writeListener) {
            try {
                writeListener.onWritePossible();
            } catch (IOException var3) {
                var3.printStackTrace();
            }

        }
    }
}
