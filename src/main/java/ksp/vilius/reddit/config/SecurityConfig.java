package ksp.vilius.reddit.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.csrf().disable()
       .authorizeRequests()
       .antMatchers("/api/auth/**")
       .permitAll()
       .anyRequest()
       .authenticated();
    }
}
