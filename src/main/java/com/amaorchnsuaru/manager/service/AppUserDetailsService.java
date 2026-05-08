package com.amaorchnsuaru.manager.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.amaorchnsuaru.manager.entity.AppUser;
import com.amaorchnsuaru.manager.repository.AppUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final AppUserRepository appUserRepository;

	public AppUserDetailsService(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser appUser = appUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						"ユーザーが見つかりません: " + username));
		String authority = "ADMIN".equals(appUser.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
		return new User(
				appUser.getUsername(),
				appUser.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority(authority)));
	}
}
