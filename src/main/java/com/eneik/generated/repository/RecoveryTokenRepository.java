package com.eneik.generated.repository;

import com.eneik.generated.entity.RecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long> {

    Optional<RecoveryToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RecoveryToken t SET t.status = :newStatus, t.usedAt = :usedAt WHERE t.token = :token AND t.status = :oldStatus")
    int markTokenUsed(@Param("token") String token,
                      @Param("oldStatus") RecoveryToken.Status oldStatus,
                      @Param("newStatus") RecoveryToken.Status newStatus,
                      @Param("usedAt") Instant usedAt);
}
