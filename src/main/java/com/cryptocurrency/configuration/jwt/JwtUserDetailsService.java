package com.cryptocurrency.configuration.jwt;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findUserOrThrowException(username);

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword().replace("{bcrypt}", ""), user.isActive(),
                true, true, true,
                userService.getAuthoritiesByUsername(username).stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .collect(Collectors.toList()));
    }

    private User findUserOrThrowException(String username){
        return userService.find(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username : " + username)
        );
    }
}
