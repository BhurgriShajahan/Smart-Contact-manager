package com.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestMethod;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider () {
		
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	/*
	 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
	 * Exception { auth.authenticationProvider(authenticationProvider()); }
	 */
	
	
	
	/*
	 * @Override protected void configure(HttpSecurity http) throws Exception{
	 * http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
	 * .antMatchers("/user/**").hasRole("USER")
	 * .antMatchers("/**").permitALL().and().formLogin().and().csrf().disable(); }
	 */
	
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		/*
//		 * http .authorizeRequests() .requestMatchers("/", "/home").permitAll()
//		 * .anyRequest().authenticated() .and() .formLogin() .loginPage("/login")
//		 * .permitAll() .and() .logout() .permitAll();
//		 */
//		http
//		.authorizeHttpRequests()
//		.requestMatchers("/admin/**")
//		.hasRole("ADMIN")
//		  .requestMatchers("/user/**")
//		  .hasRole("USER")
//		  .requestMatchers("/**")
//		  .permitAll()
//		  .and()
//		  .formLogin()
//		  .loginPage("/signin")
//				/*
//				 * .loginProcessingUrl("/dologin") .defaultSuccessUrl("/user/index")
//				 */
//		  .defaultSuccessUrl("/user/index")
//		  .and()
//		  .csrf()
//		  .disable();
////		  .failureUrl("/login-fail")
//		http.authenticationProvider(daoAuthenticationProvider());
//		DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
//		return defaultSecurityFilterChain;
//	}
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		/*
		 * http .authorizeRequests() .requestMatchers("/", "/home").permitAll()
		 * .anyRequest().authenticated() .and() .formLogin() .loginPage("/login")
		 * .permitAll() .and() .logout() .permitAll();
		 */
		http
		.authorizeHttpRequests()
		.requestMatchers("/admin/**")
		.hasRole("ADMIN")
		  .requestMatchers("/user/**")
		  .hasRole("USER")
		  .requestMatchers("/**")
		  .permitAll()
		  .and()
		  .formLogin()
		  .loginPage("/signin")
				/*
				 * .loginProcessingUrl("/dologin") .defaultSuccessUrl("/user/index")
				 */
		  .successHandler((request, response, authentication) -> {
              for (GrantedAuthority authority : authentication.getAuthorities()) {
                  if (authority.getAuthority().equals("ROLE_ADMIN")) {
                      response.sendRedirect("/admin/index");
                      return;
                  }
              }
              response.sendRedirect("/user/index");
          })
		  .and()
		  .csrf()
		  .disable();
//		  .failureUrl("/login-fail")
		http.authenticationProvider(daoAuthenticationProvider());
		DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
		return defaultSecurityFilterChain;
	}
	
	
	
	
	
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
	}
	
	
	
	
}
