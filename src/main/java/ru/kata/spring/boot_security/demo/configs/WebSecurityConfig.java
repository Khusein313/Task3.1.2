package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import ru.kata.spring.boot_security.demo.service.MyUserDetails;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetails myUserDetails;

    public WebSecurityConfig(AuthenticationSuccessHandler successUserHandler, MyUserDetails myUserDetails, PasswordEncoder passwordEncoder) {
        this.authenticationSuccessHandler = successUserHandler;
        this.myUserDetails = myUserDetails;
        this.passwordEncoder = passwordEncoder;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetails).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/login", "/logout").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/auth/login")
                .loginProcessingUrl("/process_login")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/auth/login?error=true")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login");
    }

    // аутентификация userDetailService через DaoProvider
    // метод позволяет использовать уже имеющиеся таблицы и сущности, чтоб они использовались для аутентификации
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
//Важно! Предоставит Spring данные юзера и вставляет его в SpringSecurityContext если он существует. Всё это мы обработали в UserService + метод loadUserByUsername()
        daoAuthenticationProvider.setUserDetailsService(myUserDetails);
//Этот пункт просто для преобразования паролей
        return daoAuthenticationProvider;
    }
}


/* аутентификация inMemory
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("user")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
 */