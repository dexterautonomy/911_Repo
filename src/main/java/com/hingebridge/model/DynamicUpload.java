package com.hingebridge.model;

import org.springframework.web.multipart.MultipartFile;

public class DynamicUpload
{
    private String filename;
    private MultipartFile file;
    
    public DynamicUpload(String filename, MultipartFile file)
    {
        this.filename = filename;
        this.file = file;
    }
    
    public void setFilename(String filename)
    {
        this.filename = filename;
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public void setFile(MultipartFile file)
    {
        this.file = file;
    }
    
    public MultipartFile getFile()
    {
        return file;
    }
    
}