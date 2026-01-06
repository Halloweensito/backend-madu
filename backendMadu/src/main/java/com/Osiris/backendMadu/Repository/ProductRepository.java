package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.Product;
import com.Osiris.backendMadu.Entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ===================== TIENDA =====================


    @Query("""
                SELECT p FROM Product p
                WHERE p.category.id IN :categoryIds
                AND p.status = :status
                AND p.category.status = :status
            """)
    Page<Product> findAllByCategoryIdInAndStatus(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("status") Status status,
            Pageable pageable
    );


    Optional<Product> findBySlugAndStatus(
            String slug,
            Status status
    );


    // ===================== ADMIN =====================

    boolean existsBySlug(String slug);

    Page<Product> findByStatus(Status status, Pageable pageable);

    Page<Product> findByStatusNot(Status status, Pageable pageable);

    @Query("""
                SELECT p FROM Product p
                WHERE p.status = :status
                AND (
                    LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))
                )
            """)
    Page<Product> search(
            @Param("query") String query,
            @Param("status") Status status,
            Pageable pageable
    );
}
