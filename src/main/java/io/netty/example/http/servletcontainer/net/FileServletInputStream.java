package io.netty.example.http.servletcontainer.net;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileServletInputStream extends ServletInputStream {

    private FileInputStream stream;

    private long length;

    private long readIndex = 0;

    public FileServletInputStream(File file){
        try {
            length = file.length();
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFinished() {
        return readIndex == length;
    }

    @Override
    public boolean isReady() {
        return readIndex < length;
    }

    /**
     *  实现为空，因为我已经读取完上传的所有数据，不需要异步模式
     */
    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }
}
