package Onlinestore.service;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import Onlinestore.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;
    
    public UserPrincipalDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findUserByEmail(username);
        
        if (user == null)
        {
            throw new UsernameNotFoundException("user not found");
        }
        
        return new UserPrincipal(user);
    }
}
