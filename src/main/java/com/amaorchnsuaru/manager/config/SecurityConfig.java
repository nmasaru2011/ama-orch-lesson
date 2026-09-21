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
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/web", "/web/login", "/web/register", "/css/**", "/js/**")
				.permitAll().requestMatchers(HttpMethod.POST, "/web/orch/**").hasRole("ADMIN")
				.requestMatchers("/web/orch/new", "/web/orch/*/edit").hasRole("ADMIN")
				.requestMatchers("/web/person/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/web/concert/**").hasRole("ADMIN")
				.requestMatchers("/web/concert/new", "/web/concert/*/edit").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/web/lesson/**").hasRole("ADMIN")
				.requestMatchers("/web/lesson/new", "/web/lesson/*/edit").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/web/layout/**").hasRole("ADMIN")
				.requestMatchers("/web/layout/*/edit").hasRole("ADMIN").anyRequest()
				.authenticated())
				.formLogin(form -> form.loginPage("/web/login").loginProcessingUrl("/web/login")
						.defaultSuccessUrl("/web", true).permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/web/login?logout").permitAll());

		// H2 Console: CSRF除外 + iframe許可
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
		http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
