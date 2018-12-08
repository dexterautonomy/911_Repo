package com.hingebridge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hingebridge.service.CustomUserDetailsService;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppSecurityClass extends WebSecurityConfigurerAdapter
{
    @Autowired
    CustomUserDetailsService cuds;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(cuds).passwordEncoder(getPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
	http.authorizeRequests()
        .anyRequest().permitAll()
	.and()
        .formLogin()
        .loginProcessingUrl("/j_spring_security_check")
        .usernameParameter("user")
        .passwordParameter("pswd")
        .loginPage("/login")
        .failureUrl("/login?error")
        .defaultSuccessUrl("/")
        .and()
        .logout().deleteCookies("remove").invalidateHttpSession(true)
        .logoutUrl("/j_spring_security_logout").logoutSuccessUrl("/");
    }
    
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
    	http.authorizeRequests()
    	.antMatchers("/").permitAll()
        .antMatchers("/user/**").hasRole("USER")
        .antMatchers("/admin/**").hasRole("ADMIN")
        .and()
        .formLogin()
        .loginProcessingUrl("/j_spring_security_check")
        .usernameParameter("user")
        .passwordParameter("pswd")
        .loginPage("/login").permitAll()
        .failureUrl("/login?error")
        .defaultSuccessUrl("/test")
        .and()
        .logout().deleteCookies("remove").invalidateHttpSession(true)
        .logoutUrl("/j_spring_security_logout").logoutSuccessUrl("/");
    }
    */
    
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder()
    {
    	return new BCryptPasswordEncoder();
    }
}