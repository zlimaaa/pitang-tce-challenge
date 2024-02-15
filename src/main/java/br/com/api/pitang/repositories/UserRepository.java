package br.com.api.pitang.repositories;

import br.com.api.pitang.data.models.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findDistinctByLogin(String login);
    Optional<User> findDistinctById(Long id);
    Long countByLoginAndIdNot(String login, Long id);
    Long countByEmailAndIdNot(String email, Long id);

    @Query("SELECT u, COALESCE(SUM(c.usageCounter), 0) AS totalUsageCounter FROM User u LEFT JOIN u.cars c GROUP BY u ORDER BY totalUsageCounter DESC, u.login ASC")
    Page<Object[]> findAllUsersOrderByTotalUsageCounterAndLogin(Pageable pageable);

    default Page<User> findAllUsersOrderByTotalUsageCounterAndLoginPage(Pageable pageable) {
        Page<Object[]> usersPage = findAllUsersOrderByTotalUsageCounterAndLogin(pageable);

        List<User> users = usersPage.getContent().stream()
                .map(objectArray -> (User) objectArray[0])
                .collect(Collectors.toList());

        return new PageImpl<>(users, pageable, usersPage.getTotalElements());
    }

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
