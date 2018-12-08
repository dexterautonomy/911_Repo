package com.hingebridge.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="userrole")
public class UserRoleClass implements Serializable
{
    @Id
    @Column(name="userid")
    private Long id1;
    @Column(name="roleid")
    private int id2;
	
    public UserRoleClass(){}
	
    public UserRoleClass(Long id1, int id2)
    {
    	this.id1 = id1;
    	this.id2 = id2;
    }
    
    public void setId1(Long value)
    {
        this.id1=value;
    }
    
    public Long getId1()
    {
        return id1;
    }
    
    public void setId2(int value)
    {
        this.id2=value;
    }
    
    public int getId2()
    {
        return id2;
    }
}