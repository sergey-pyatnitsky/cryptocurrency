package com.cryptocurrency.controller;

import com.cryptocurrency.configuration.jwt.JwtTokenUtil;
import com.cryptocurrency.entity.domain.Profile;
import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.enums.Role;
import com.cryptocurrency.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService jwtInMemoryUserDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public String createAuthenticationToken(@RequestBody User user) {
        final UserDetails userDetails = jwtInMemoryUserDetailsService
                .loadUserByUsername(user.getUsername());
        Authentication authentication =
                authenticate(user.getUsername(), user.getPassword(), userDetails.getAuthorities());

        return jwtTokenUtil.createToken(authentication);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public void registerEmployee(@RequestBody Profile profile, @RequestParam("locale") String locale) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag(locale));
        if (!userService.registerUser(profile.getUser(),
                profile.getUser().getPassword(),
                profile.getName(), Role.ADMIN, profile.getEmail()))
            throw new BadCredentialsException("Registration error");
    }

    private Authentication authenticate(String username,
                                        String password,
                                        Collection<? extends GrantedAuthority> authorities) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password, authorities));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (DisabledException e) {
            throw new DisabledException("login.user.disabled", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("login.invalid_credentials", e);
        }
    }
}