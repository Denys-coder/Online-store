package Onlinestore.service;

import Onlinestore.entity.User;
import Onlinestore.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }
    
    public boolean isEmailUnique(User user)
    {
        return !userRepository.existsByEmail(user.getEmail());
    }
    
    public boolean isTelephoneNumberUnique(User user)
    {
        return !userRepository.existsByTelephoneNumber(user.getTelephoneNumber());
    }
}
