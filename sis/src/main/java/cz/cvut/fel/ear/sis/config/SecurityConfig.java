package cz.cvut.fel.ear.sis.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.security.AuthenticationFailure;
import cz.cvut.fel.ear.sis.security.AuthenticationSuccess;
import cz.cvut.fel.ear.sis.service.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity     // Allows Spring Security
@EnableMethodSecurity // Allow methods to be secured using annotation @PreAuthorize and @PostAuthorize
public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ObjectMapper objectMapper;



    private final PersonRepository personRepository;


    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        final AuthenticationSuccess authSuccess = authenticationSuccess();

        http.authorizeRequests((auth) -> auth.anyRequest().permitAll())
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(conf -> conf.configurationSource(corsConfigurationSource()))
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(fl -> fl.loginProcessingUrl("/login")
                        .successHandler(authSuccess)
                        .failureHandler(authenticationFailureHandler())
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll())
                .logout(lgt -> lgt.logoutSuccessHandler(authSuccess))
                .authenticationProvider(authenticationProvider());
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(personRepository);
    }


    public SecurityConfig(ObjectMapper objectMapper, PersonRepository personRepository) {
        this.objectMapper = objectMapper;
        this.personRepository = personRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AuthenticationSuccess authSuccess = authenticationSuccess();
        // Allow through everything, it will be dealt with using security annotations on methods
        http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
            // Return 401 by default when attempting to access a secured endpoint
            .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .csrf(AbstractHttpConfigurer::disable)
            // Enable CORS
            .cors(conf -> conf.configurationSource(corsConfigurationSource()))
            .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            // Use custom success and failure handlers
//            .formLogin(fl -> fl.successHandler(authSuccess)
//                               .failureHandler(authenticationFailureHandler()))
            .formLogin(fl->fl.loginProcessingUrl("/login")
                    .successHandler(authSuccess)
                    .failureHandler(authenticationFailureHandler())
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll())

            .logout(lgt -> lgt.logoutSuccessHandler(authSuccess));
        return http.build();
    }

    private AuthenticationFailure authenticationFailureHandler() {
        return new AuthenticationFailure(objectMapper);
    }

    private AuthenticationSuccess authenticationSuccess() {
        return new AuthenticationSuccess(objectMapper);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        // We're allowing all methods from all origins so that the application API is usable also by other clients
        // than just the UI.
        // This behavior can be restricted later.
        CorsConfiguration configuration = new CorsConfiguration();
        // AllowCredentials requires a particular origin configured, * is rejected by the browser
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.addExposedHeader(HttpHeaders.LOCATION);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
