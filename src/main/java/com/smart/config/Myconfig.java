package com.smart.config;

import java.beans.Customizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.smart.entities.User;

@Configuration
@EnableWebSecurity
public class Myconfig {

	@Bean
	public UserDetailsService getDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(this.getDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	/*
	 * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
	 * Exception { http .authorizeRequests() .requestMatchers("/public").permitAll()
	 * // Allow access to public URL .anyRequest().authenticated() .and()
	 * .formLogin() // Enable form login .loginPage("/login") // Login page URL
	 * .permitAll(); // Allow access to login page return http.build(); }
	 */

	/*
	 * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
	 * throws Exception { return http .csrf().disable() .authorizeRequests()
	 * .requestMatchers("/admin/**").hasRole("ADMIN")
	 * .requestMatchers("/user**").hasRole("USER")
	 * .requestMatchers("/**").permitAll().and(). build();
	 * 
	 * }
	 */

	/*
	 * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
	 * throws Exception { return http .csrf(csrf -> csrf.disable()) // (1)
	 * .authorizeRequests( auth -> {
	 * auth.requestMatchers("/admin/**").hasRole("ADMIN");
	 * auth.requestMatchers("/user/**").hasRole("USER");
	 * auth.requestMatchers("/**").permitAll(); }) .httpBasic() .build(); }
	 */
	
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // Use correct method for authorization rules
                .requestMatchers("/admin/**").hasRole("ROLE_ADMIN") // Check for role with prefix
                .requestMatchers("/user/**").hasRole("ROLE_USER")
                .anyRequest().authenticated() // Require authentication for other requests
            .and()
            .formLogin() // Enable form-based login
                .permitAll() // Allow access to login page
            .and()
            .csrf().disable(); // Disable CSRF for simplicity (consider enabling in production)
        return http.build();
    }
}
