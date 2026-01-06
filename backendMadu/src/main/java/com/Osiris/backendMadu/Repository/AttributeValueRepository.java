package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    boolean existsByAttributeIdAndSlug(Long attributeId, String slug);

    @Query("SELECT COALESCE(MAX(av.position), 0) FROM AttributeValue av WHERE av.attribute.id = :attributeId")
        // 👈 Agregado COALESCE
    Integer findMaxPositionByAttributeId(@Param("attributeId") Long attributeId);

    // Recomendación: Agrega el parámetro @Param para evitar errores de compilación en algunas versiones de Spring
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AttributeValue av SET av.position = :newPosition " +
            "WHERE av.id = :valueId AND av.attribute.id = :attributeId")
    void updatePositionIfBelongsToAttribute(
            @Param("valueId") Long valueId,
            @Param("attributeId") Long attributeId,
            @Param("newPosition") Integer newPosition
    );
}
