package ck.test.demo.security;

import ck.test.demo.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inject your custom handlers here
    @Autowired
    private  AccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private  AuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Inject the filter

    @Autowired
    private CustomUserDetailsService customUserDetailsService;



    public SecurityConfig(AccessDeniedHandler customAccessDeniedHandler,
                          AuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints accessible by anyone
                        .requestMatchers(HttpMethod.POST,"/api/products/create").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/register").permitAll()
                        //enable actuator end point based on ROLE
                        .requestMatchers(HttpMethod.GET, "/actuator/metrics/system.cpu.usage").hasAuthority("ROLE_VIEWER")
                        // secured endpoints - requires ADMIN role
                        .requestMatchers(HttpMethod.GET,"/actuator/env").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/actuator/loggers").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/actuator/heapdump").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/actuator/threaddump").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/customer/*").permitAll()
                        // All other requests require the user to be authenticated
                        .anyRequest().authenticated()
                );

        // 3. Set Session Management to Stateless
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        // Use the custom handler for 403 Forbidden errors (Access Denied)
                        .accessDeniedHandler(customAccessDeniedHandler)
                        // Use the custom entry point for 401 Unauthorized errors (Authentication required)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
        );
        http.userDetailsService(customUserDetailsService);
        // Add the custom JWT filter before Spring's standard UsernamePasswordAuthenticationFilter
        http.addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
