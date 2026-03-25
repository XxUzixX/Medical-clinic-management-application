package com.example.clinic.config;

import com.example.clinic.security.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final UserDetailsService userDetailsService;

    private final CustomAuthenticationSuccessHandler successHandler;

    public SpringSecurity(UserDetailsService userDetailsService, CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .requestMatchers("/doctors").permitAll()
                                .requestMatchers("/users").hasRole("ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/doctorhome/**").hasRole("DOCTOR")
                                .requestMatchers("/userhome/**").hasRole("USER")
                                .requestMatchers("/adminhome").hasRole("ADMIN")
                                .requestMatchers("/register_doctor").hasRole("ADMIN")
                                .requestMatchers("/doctor/patients").hasRole("DOCTOR")
                                .requestMatchers("/doctor/prescriptions/**").hasRole("DOCTOR")
                                .requestMatchers("/appointments/new").hasRole("USER")
                                .requestMatchers("/appointments/doctor/**").hasRole("DOCTOR")
                                .requestMatchers("/appointments/patient/**").hasRole("USER")
                                .requestMatchers("/appointments/reject/**").hasAnyRole("USER","DOCTOR")
                                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                                .requestMatchers("/ai/**").hasRole("DOCTOR")
                                .requestMatchers("/doctor/patients**").hasRole("DOCTOR")
                                .requestMatchers("/booking/**").hasRole("USER")
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .successHandler(successHandler)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new org.springframework.security.web.util.matcher.RequestMatcher() {
                                    @Override
                                    public boolean matches(HttpServletRequest request) {
                                        return "GET".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/logout");
                                    }
                                })
                                .logoutSuccessUrl("/login?logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                                .permitAll()
                )
                .addFilterAfter(new CsrfEagerTokenFilter(), BasicAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    private static class CsrfEagerTokenFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            filterChain.doFilter(request, response);
        }
    }
}
