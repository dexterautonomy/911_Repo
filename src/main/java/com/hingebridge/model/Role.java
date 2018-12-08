package com.hingebridge.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="roleclass")
public class Role implements Serializable
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @Column(name="rolename")
    private String rolename;
	
    public Role(){}
	
    public Role(String rolename)
    {
        this.rolename=rolename;
    }
    
    public void setRolename(String rolename)
    {
        this.rolename=rolename;
    }
    
    public String getRolename()
    {
        return rolename;
    }
    
    public int getId()
    {
        return id;
    }
}