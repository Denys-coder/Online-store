package Onlinestore.entity;

import Onlinestore.model.RoleName;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotBlank(message = "should not be blank")
    @Size(min = 1, max = 30, message = "should be from 1 to 30 symbols")
    private String name;

    @NotBlank(message = "should not be blank")
    @Size(min = 1, max = 30, message = "should be from 1 to 30 symbols")
    private String surname;

    @Column(unique = true)
    @NotBlank(message = "should not be blank")
    @Size(min = 5, max = 32, message = "should be from 5 to 32 symbols")
    @Email(message = "this is not an email")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "should not be blank")
    @Size(min = 8, max = 64, message = "should be from 8 to 64 symbols")
    private String password;

    @Column(name = "telephone_number", nullable = false, unique = true)
    @NotNull(message = "should not be blank")
    @Pattern(regexp = "\\d{6,12}", message = "telephone number must be from 6 to 12 digits")
    private String telephoneNumber;

    @Size(min = 3, max = 50, message = "should be from 3 to 50 symbols")
    private String country;

    @Size(min = 10, max = 100, message = "should be from 10 to 100 symbols")
    private String address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userId")
    private Set<Order> orders;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    public User() {
        orders = new HashSet<>();
    }

    public User(String name, String surname, String email, String password, String telephoneNumber, String country, String address, Set<Order> orders, RoleName roleName) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.telephoneNumber = telephoneNumber;
        this.country = country;
        this.address = address;
        this.orders = orders;
        this.roleName = roleName;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void deleteOrderById(int orderId) {
        Iterator<Order> orderIterator = orders.iterator();
        while (orderIterator.hasNext()) {
            if (orderIterator.next().getId() == orderId) {
                orderIterator.remove();
                break;
            }
        }
    }

    public void deleteOrdersByItemId(int itemIdToDelete) {
        Iterator<Order> orderIterator = orders.iterator();
        while (orderIterator.hasNext()) {
            if (orderIterator.next().getItem().getId() == itemIdToDelete) {
                orderIterator.remove();
                break;
            }
        }
    }
}
