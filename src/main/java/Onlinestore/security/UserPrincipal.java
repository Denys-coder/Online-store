package Onlinestore.security;

import Onlinestore.entity.User;
import Onlinestore.model.RoleNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails
{
    @Getter
    @Setter
    private User user;
    
    public UserPrincipal(User user)
    {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        RoleNames roleNames = user.getRoleNames();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleNames.name()));
        return authorities;
    }
    
    @Override
    public String getUsername()
    {
        return user.getEmail();
    }
    
    @Override
    public String getPassword()
    {
        return user.getPassword();
    }
    
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }
    
    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
