package com.eneik.generated.repository;

import com.eneik.generated.entity.EpidemiologicalMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpidemiologicalMaterialRepository extends JpaRepository<EpidemiologicalMaterial, Long> {

    List<EpidemiologicalMaterial> findByPathogenType(String pathogenType);

    @Query("SELECT e FROM EpidemiologicalMaterial e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EpidemiologicalMaterial> searchByKeyword(@Param("keyword") String keyword);
}
