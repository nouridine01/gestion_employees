package com.example.demo.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static javax.swing.text.html.FormSubmitEvent.MethodType.GET;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private DataSource datasource;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//on definit ici les users qui ont le droit d'acceder à l'application
		auth.inMemoryAuthentication().withUser("admin")
				.password("{noop}passer").roles("USER","ADMIN");


	}

	@Override
	public void configure(HttpSecurity http) throws Exception {


		http.formLogin().loginPage("/login").permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET")).logoutSuccessUrl("/");
		http.authorizeRequests().antMatchers("/*").hasRole("ADMIN");
		http.exceptionHandling().accessDeniedPage("/403");

	}

}
