package Onlinestore.config;

import Onlinestore.service.UserPrincipalDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    private final UserPrincipalDetailsService userPrincipalDetailsService;
    private final DaoAuthenticationProvider authenticationProvider;
    
    public SecurityConfiguration(UserPrincipalDetailsService userPrincipalDetailsService,
                                 DaoAuthenticationProvider authenticationProvider)
    {
        this.userPrincipalDetailsService = userPrincipalDetailsService;
        this.authenticationProvider = authenticationProvider;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    {
        auth.authenticationProvider(authenticationProvider);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .authorizeRequests()
                    .antMatchers("/about").permitAll()
                    .antMatchers("/profile/**").authenticated()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/cart/**").hasRole("USER")
                    .antMatchers("/registration").anonymous()
                    .antMatchers("/catalog/**").permitAll()
                .and()
                    .formLogin()
                    .loginProcessingUrl("/login")
                    .loginPage("/login").permitAll()
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/")
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                .and()
                    .rememberMe()
                    .tokenValiditySeconds(2592000) // 30 days
                    .key("mySecret!")
                    .rememberMeParameter("remember-me")
                    .userDetailsService(userPrincipalDetailsService);
    }
}
