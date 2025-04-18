package Onlinestore.repository;

import Onlinestore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelephoneNumber(String telephoneNumber);
}
