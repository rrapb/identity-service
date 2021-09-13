package com.ubt.identityservice.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Value("${jwt.master.user:gymmsMasterUser}")
	private String jwtMasterUser;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if(username != null && username.equals(jwtMasterUser)) {
			return new org.springframework.security.core.userdetails.User(username, "", new ArrayList<>());
		}
		log.error("Username: \"{}\" not found!", username);
		throw new UsernameNotFoundException("Username not found!");
	}
}
