package com.hingebridge.service;

import com.hingebridge.model.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hingebridge.model.UserClass;
import java.util.Set;

@SuppressWarnings("serial")
public class CustomUserDetails implements UserDetails
{
    private final UserClass uc;
	
    public CustomUserDetails(UserClass uc)
    {
    	this.uc = uc;
    }
	
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
	List<SimpleGrantedAuthority> gaList=new ArrayList<>();
        Set<Role> roleSet = uc.getRole();
        
        for(Role role : roleSet)
        {
            gaList.add(new SimpleGrantedAuthority("ROLE_" + role.getRolename()));
        }
        
        return gaList;
    }

    @Override
    public String getPassword()
    {
	return uc.getPassword();
    }

    @Override
    public String getUsername()
    {
	return uc.getUsername();
    }

    @Override
    public boolean isAccountNonExpired()
    {
	return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
	return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
	return true;
    }

    @Override
    public boolean isEnabled()
    {
	return true;
    }
}