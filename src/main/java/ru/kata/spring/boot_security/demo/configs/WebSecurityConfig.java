package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.MyUserDetails;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetails myUserDetails;

    public WebSecurityConfig(SuccessUserHandler successUserHandler, MyUserDetails myUserDetails, PasswordEncoder passwordEncoder) {
        this.successUserHandler = successUserHandler;
        this.myUserDetails = myUserDetails;
        this.passwordEncoder = passwordEncoder;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetails).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/users/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
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