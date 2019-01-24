package com.hingebridge.model;

public class DynamicContent
{
    private String content;
    private String content2;
    private Long pos;
    private Long cid;
    private Long sid;
    private String title;
    private int page;
    private int pg;
    private int p2;
    
    public DynamicContent(Long pos, String title, int page, int pg, String content)
    {
        this.content = content;
        this.pos = pos;
        this.title = title;
        this.page = page;
        this.pg = pg;
    }
    
    public DynamicContent(Long pos, String title, int page, int pg, String content, String content2)
    {
        this.content = content;
        this.content2 = content2;
        this.pos = pos;
        this.title = title;
        this.page = page;
        this.pg = pg;
    }
    
    public DynamicContent(Long pos, Long cid, String title, int page, int pg, String content)
    {
        this.content = content;
        this.pos = pos;
        this.cid = cid;
        this.title = title;
        this.page = page;
        this.pg = pg;
    }
    
    public DynamicContent(Long pos, String title, int page, int pg, String content, Long sid)
    {
        this.content = content;
        this.sid = sid;
        this.pos = pos;
        this.title = title;
        this.page = page;
        this.pg = pg;
    }
    
    public DynamicContent(Long pos, Long cid, Long sid, String title, int p2, int pg, int page, String content)
    {
        this.pos = pos;
        this.cid = cid;
        this.sid = sid;
        this.title = title;
        this.p2 = p2;
        this.pg = pg;
        this.page = page;
        this.content = content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public void setContent2(String content2)
    {
        this.content2 = content2;
    }
    
    public String getContent2()
    {
        return content2;
    }
    
    public void setPos(Long pos)
    {
        this.pos = pos;
    }
    
    public Long getPos()
    {
        return pos;
    }
    
    public void setCid(Long cid)
    {
        this.cid = cid;
    }
    
    public Long getCid()
    {
        return cid;
    }
    
    public void setSid(Long sid)
    {
        this.sid = sid;
    }
    
    public Long getSid()
    {
        return sid;
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
    
    public void setP2(int p2)
    {
        this.p2 = p2;
    }
    
    public int getP2()
    {
        return p2;
    }
}