package Onlinestorerestapi.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String surname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(name = "telephone_number", nullable = false, unique = true, length = 15)
    private String telephoneNumber;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(nullable = false, length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, length = 20)
    private RoleName roleName;

    public User(String name, String surname, String email, String password, String telephoneNumber, String country, String address, RoleName roleName) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.telephoneNumber = telephoneNumber;
        this.country = country;
        this.address = address;
        this.roleName = roleName;
    }
}
