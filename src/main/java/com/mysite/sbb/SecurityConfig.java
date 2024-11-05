package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mysite.sbb.user.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AuthorizationCodeGrantRequestEntityConverter converter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
        tokenResponseClient.setRequestEntityConverter(converter);

        http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/manage/**").hasRole("ADMIN")
                .requestMatchers(new AntPathRequestMatcher("/messages/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
            )
            .csrf((csrf) -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2-console/**"), 
                    new AntPathRequestMatcher("/food/**"),
                    new AntPathRequestMatcher("/messages/send"),  // 메시지 전송 경로에 대한 CSRF 보호 무시
                    new AntPathRequestMatcher("/messages/delete/**"),  // 메시지 삭제 경로에 대해 CSRF 무시
                    new AntPathRequestMatcher("/workout/record_routine")  // 루틴 데이터 저장 경로에 대해 CSRF 무시

                )
            )
            .headers((headers) -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )
            .formLogin((formLogin) -> formLogin
                .loginPage("/user/login")
                .defaultSuccessUrl("/", true) // 로그인 성공 시 메인 페이지로 리디렉트
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/")  // 로그인 성공 후 메인 페이지로 이동
                    .tokenEndpoint(tokenEndpoint -> 
                        tokenEndpoint.accessTokenResponseClient(tokenResponseClient) // 카카오 인증 처리
                    )
                    .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint.userService(customOAuth2UserService)
                    )
            );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
