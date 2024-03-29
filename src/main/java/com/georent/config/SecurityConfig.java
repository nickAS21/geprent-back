package com.georent.config;

import com.georent.security.JwtAuthenticationFilter;
import com.georent.security.JwtEntryPointUnauthorizedHandler;
import com.georent.service.GeoRentUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final transient GeoRentUserDetailsService userDetailsService;
    private final transient JwtEntryPointUnauthorizedHandler authEntryPoint;
    private final transient JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(final GeoRentUserDetailsService geoRentUserDetailsService,
                          final JwtEntryPointUnauthorizedHandler authEntryPoint,
                          final JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = geoRentUserDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(authEntryPoint)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(
                            "/login**",
                            "/search/**",
                            "/register**",
                            "/lot/**",
                            "/forgotpassword/**",
                            "/console/**",
                            "/v2/api-docs",
                            "/webjars/**",
                            "/swagger-resources/**",
                            "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                .headers()
                    .frameOptions()
                    .sameOrigin();
    }
}
