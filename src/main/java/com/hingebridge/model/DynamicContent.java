package com.hingebridge.model;

public class DynamicContent
{
    private String content;
    private Long pos;
    private String title;
    private int page;
    private int pg;
    
    public DynamicContent(String content, Long pos, String title, int page, int pg)
    {
        this.content = content;
        this.pos = pos;
        this.title = title;
        this.page = page;
        this.pg = pg;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setPos(Long pos)
    {
        this.pos = pos;
    }
    
    public Long getPos()
    {
        return pos;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public int getPage()
    {
        return page;
    }
    
    public void setPg(int pg)
    {
        this.pg = pg;
    }
    
    public int getPg()
    {
        return pg;
    }
    
}