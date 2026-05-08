package com.amaorchnsuaru.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/orch/**").hasRole("ADMIN")
						.requestMatchers("/orch/new", "/orch/*/edit").hasRole("ADMIN")
						.requestMatchers("/person/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/concert/**").hasRole("ADMIN")
						.requestMatchers("/concert/new", "/concert/*/edit").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/lesson/**").hasRole("ADMIN")
						.requestMatchers("/lesson/new", "/lesson/*/edit").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.permitAll());

		// H2 Console: CSRF除外 + iframe許可
		http.csrf(csrf -> csrf
				.ignoringRequestMatchers("/h2-console/**"));
		http.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.sameOrigin()));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
