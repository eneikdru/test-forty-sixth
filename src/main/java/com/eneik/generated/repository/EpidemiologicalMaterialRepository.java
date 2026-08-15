package com.eneik.generated.repository;

import com.eneik.generated.entity.EpidemiologicalMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpidemiologicalMaterialRepository extends JpaRepository<EpidemiologicalMaterial, Long>, JpaSpecificationExecutor<EpidemiologicalMaterial> {

    List<EpidemiologicalMaterial> findByPathogenType(String pathogenType);

    @Query("SELECT e FROM EpidemiologicalMaterial e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EpidemiologicalMaterial> searchByKeyword(@Param("keyword") String keyword);

    static Specification<EpidemiologicalMaterial> buildSearchSpecification(String query, String pathogenType) {
        return (root, criteriaQuery, cb) -> {
            var predicates = cb.conjunction();
            if (pathogenType != null && !pathogenType.trim().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("pathogenType"), pathogenType.trim().toUpperCase()));
            }
            if (query != null && !query.trim().isEmpty()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                var titleLike = cb.like(cb.lower(root.get("title")), pattern);
                var contentLike = cb.like(cb.lower(root.get("content")), pattern);
                predicates = cb.and(predicates, cb.or(titleLike, contentLike));
            }
            return predicates;
        };
    }
}
