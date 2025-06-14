package org.example.greetingright.repository;

import jakarta.transaction.Transactional;
import org.example.greetingright.entity.RefreshToken;
import org.example.greetingright.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(String refreshToken);

    @Transactional // Make sure this action is done in one complete step (transaction)
    @Modifying(clearAutomatically = true) //This query changes the database (not just reads), and clears old saved data after
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token")
    void deleteByToken(@Param("token") String token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUser_Id(Long userId);

}
