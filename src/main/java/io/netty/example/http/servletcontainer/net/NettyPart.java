package io.netty.example.http.servletcontainer.net;

import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class NettyPart  implements Part {

    private InterfaceHttpData data;

    public NettyPart(InterfaceHttpData data){
        this.data = data;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            return new FileInputStream(upload.getFile());
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){
            return null;
        }else {
            return null;
        }
    }

    @Override
    public String getContentType() {
        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            return upload.getContentType();
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){

            return null;
        }else{
            return null;
        }
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public String getSubmittedFileName() {

        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            return upload.getFilename();
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){

            return null;
        }else{
            return null;
        }
    }

    @Override
    public long getSize() {
        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            try {
                return upload.getFile().length();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){

            return -1;
        }else{
            return -1;
        }
    }

    @Override
    public void write(String fileName) throws IOException {

        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            //文件名要配置
            upload.getFile().renameTo(new File(fileName));
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){

        }else{
        }
    }

    @Override
    public void delete() throws IOException {
        data.release();
    }

    @Override
    public String getHeader(String name) {
        InterfaceHttpData.HttpDataType type = data.getHttpDataType();
        if(type.equals(InterfaceHttpData.HttpDataType.FileUpload)){
            FileUpload upload = (FileUpload) data;
            //文件名要配置
            return null;
        }else if(type.equals(InterfaceHttpData.HttpDataType.Attribute)){

            return null;
        }else{
            return null;
        }

    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }
}
