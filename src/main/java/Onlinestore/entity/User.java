package Onlinestore.entity;

import Onlinestore.model.OnlyDigitsConstraint;
import Onlinestore.model.RoleNames;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity(name = "user")
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User
{
    @Getter
    @Setter
    @Column(nullable = false)
    @NotBlank
    @Size(min = 1, max = 30)
    private String name;
    
    @Getter
    @Setter
    @NotBlank
    @Size(min = 1, max = 30)
    private String surname;
    
    @Id
    @Getter
    @Setter
    @Column(unique = true)
    @NotBlank
    @Size(min = 5, max = 32)
    @Email(message = "this is not an email")
    private String email;
    
    @Getter
    @Setter
    @Column(nullable = false)
    @NotNull
    @Size(min = 8, max = 64)
    private String password;
    
    @Getter
    @Setter
    @Transient
    @NotNull
    private String repeatedPassword = "";
    
    @Getter
    @Setter
    @Column(name = "telephone_number", nullable = false, unique = true)
    @NotNull
    @Size(min = 6, max = 12)
    @OnlyDigitsConstraint
    private String telephoneNumber;
    
    @Getter
    @Setter
    @Size(min = 3, max = 50)
    private String country;
    
    @Getter
    @Setter
    @Size(min = 10, max = 100)
    private String address;
    
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Order> orders;
    
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    RoleNames roleNames;
}