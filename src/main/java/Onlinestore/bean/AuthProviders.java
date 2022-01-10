package Onlinestore.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthProviders
{
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    
    public AuthProviders(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService)
    {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        
        return daoAuthenticationProvider;
    }
}
