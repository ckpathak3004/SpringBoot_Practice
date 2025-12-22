package ck.test.demo.repository;
import ck.test.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA generates: SELECT * FROM users WHERE username = ?
	Optional<User> findByUsername(String username);
}