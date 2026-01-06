package ck.test.demo.repository;
import ck.test.demo.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA generates: SELECT * FROM users WHERE username = ?
	Optional<User> findByUsername(String username);

    //get user role detail
    @Query(value = "SELECT u.username, r.name FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON ur.role_id = r.id " +
            "WHERE u.username = :email", nativeQuery = true)
    List<Object[]> findUserDetailsByEmail(@Param("email") String email);


}