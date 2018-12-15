package com.hingebridge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hingebridge.model.UserClass;
import com.hingebridge.repository.UserClassRepo;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    @Autowired
    UserClassRepo ucr;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<UserClass> uc = ucr.findByUsername(username);
	
	if(uc == null)
	{
            throw new UsernameNotFoundException("Username does not exist");
	}
		
	return new CustomUserDetails(uc.get());
    }
}