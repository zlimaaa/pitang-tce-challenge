package br.com.api.pitang.repositories;

import br.com.api.pitang.data.models.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findDistinctByLogin(String login);
    Optional<User> findDistinctById(Long id);
    Long countByLoginAndIdNot(String login, Long id);
    Long countByEmailAndIdNot(String email, Long id);
    @Modifying
    @Query("update User set lastLogin = :lastLogin where id = :id")
    void updateLastLogin(@Param(value = "id") Long id,
                         @Param(value = "lastLogin") LocalDateTime lastLogin);

    /**
     * query para deletar os usuarios inativos a mais de 30 dias,
     * serao deletados os usuarios que estao a mais de 30 dias sem fazer login
     * ou os usuario que nunca fizeram login e a data de criacao tem mais de 30 dias
     * @param deadline
     */
    @Modifying
    @Query("delete from User u where coalesce(u.lastLogin, u.createdAt) < :deadline")
    void deleteInactiveUsers(@Param(value = "deadline") LocalDateTime deadline);

}
