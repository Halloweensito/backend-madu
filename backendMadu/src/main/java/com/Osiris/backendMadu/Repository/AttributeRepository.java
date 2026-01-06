package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    boolean existsBySlug(String slug);


    @Query("SELECT COALESCE(MAX(a.position), 0) FROM Attribute a")
    Integer findMaxPosition();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Attribute a SET a.position = :newPosition WHERE a.id = :id")
    void updateAttributePosition(Long id, Integer newPosition);

    // También actualiza el findAll para que devuelva siempre en orden
    @Query("SELECT a FROM Attribute a ORDER BY a.position ASC")
    List<Attribute> findAllOrderByPosition();
}
