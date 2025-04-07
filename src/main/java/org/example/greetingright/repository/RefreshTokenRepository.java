package org.example.greetingright.repository;

import org.example.greetingright.entity.RefreshToken;
import org.example.greetingright.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(String refreshToken);

    void deleteByToken(String token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUser_Id(Long userId);

}
