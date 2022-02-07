package Onlinestore.entity;

import Onlinestore.model.OnlyDigitsConstraint;
import Onlinestore.model.RoleNames;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;
    
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
    private String repeatedPassword;
    
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
    
    public User()
    {
        repeatedPassword = "";
        orders = new ArrayList<>();
    }
    
    public User(String name, String surname, String email, String password, String repeatedPassword, String telephoneNumber, String country, String address, List<Order> orders, RoleNames roleNames)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.telephoneNumber = telephoneNumber;
        this.country = country;
        this.address = address;
        this.orders = orders;
        this.roleNames = roleNames;
    }
    
    public void addOrder(Order order)
    {
        orders.add(order);
    }
    
    public void deleteOrder(int orderId)
    {
        for (int i = 0; i < orders.size(); i++)
        {
            if (orders.get(i).getId() == orderId)
            {
                orders.remove(i);
                break;
            }
        }
    }
}