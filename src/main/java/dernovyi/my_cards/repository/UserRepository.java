package dernovyi.my_cards.repository;

import dernovyi.my_cards.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserByUserId(String userId);
    void deleteByEmail(String email);
}
