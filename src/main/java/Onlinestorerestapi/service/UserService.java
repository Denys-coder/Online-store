package Onlinestorerestapi.service;

import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getCurrentUser() {
        UserPrincipal userPrincipal = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return userPrincipal.getUser();
    }

}
