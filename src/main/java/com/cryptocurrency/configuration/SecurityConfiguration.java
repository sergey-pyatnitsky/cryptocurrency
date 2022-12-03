package com.cryptocurrency.configuration;

import com.cryptocurrency.configuration.jwt.AuthenticationEntryPoint;
import com.cryptocurrency.configuration.jwt.JwtRequestFilter;
import com.cryptocurrency.configuration.jwt.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().cors().and()
                .authorizeRequests()
                .antMatchers("/resources/**", "/login/**", "/oauth2/**", "/auth/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().disable().httpBasic().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization").and()
                .defaultSuccessUrl("/page")
                .failureUrl("/login?unauthorized");
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
