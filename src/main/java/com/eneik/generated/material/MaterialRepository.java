package com.eneik.generated.material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    @Modifying
    @Query("UPDATE Material m SET m.status = :newStatus WHERE m.id = :id AND m.status = :expectedStatus")
    int updateStatusConditionally(@Param("id") UUID id, @Param("expectedStatus") String expectedStatus, @Param("newStatus") String newStatus);

    @Modifying
    @Query("UPDATE Material m SET m.status = :newStatus WHERE m.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("newStatus") String newStatus);
}
