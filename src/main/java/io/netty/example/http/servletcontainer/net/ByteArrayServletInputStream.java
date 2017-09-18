package io.netty.example.http.servletcontainer.net;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * 通过byte [] 构造一个 ServletInputStream
 */
public class ByteArrayServletInputStream extends ServletInputStream {

    private byte [] data;
    private int readIndex;

    public ByteArrayServletInputStream(byte [] data){
        this.data = data;
    }

    @Override
    public boolean isFinished() {
        return readIndex == data.length;
    }

    @Override
    public boolean isReady() {
        return readIndex < data.length;
    }

    /**
     *     实现为空，因为我已经读取完上传的所有数据，不需要异步模式
     */
    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return data[readIndex++];
    }
}
