package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.Category;
import com.Osiris.backendMadu.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    // ===================== TIENDA =====================

    List<Category> findAllByStatus(Status status);

    Optional<Category> findByIdAndStatus(
            Long id,
            Status status
    );

    Optional<Category> findBySlugAndStatus(
            String slug,
            Status status
    );

    List<Category> findByParentIsNullAndStatus(
            Status status
    );


    // 1. Para listas planas (Dropdowns, Tablas simples)
    // Trae ACTIVE e INACTIVE, excluye ARCHIVED
    List<Category> findByStatusNot(Status status);

    // 2. Para vista de Árbol Admin (Solo raíces no borradas)
    // Trae raíces que sean ACTIVE o INACTIVE
    List<Category> findByParentIsNullAndStatusNot(Status status);


    boolean existsBySlug(String slug);

}
