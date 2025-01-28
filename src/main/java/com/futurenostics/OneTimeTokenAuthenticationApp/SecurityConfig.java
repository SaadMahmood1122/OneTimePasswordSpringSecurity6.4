package com.futurenostics.OneTimeTokenAuthenticationApp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager() {

        var user = User.withUsername("khan").password("{noop}password").build();
        return new InMemoryUserDetailsManager(user);
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/ott/login").permitAll()
                                        .requestMatchers("/ott/sent").permitAll()
                                         .anyRequest().authenticated()
                        )

                .formLogin(Customizer.withDefaults())
                // adding one time token
                .oneTimeTokenLogin(Customizer.withDefaults())
                .build();
    }
}
