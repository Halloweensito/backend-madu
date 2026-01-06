package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Service.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    // ===================== READ =====================

    /**
     * Listar todos los atributos con sus valores
     * GET /api/attributes
     */
    @GetMapping
    public ResponseEntity<List<AttributeResponse>> getAll() {
        return ResponseEntity.ok(attributeService.findAll());
    }

    /**
     * Obtener un atributo por ID
     * GET /api/attributes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attributeService.findById(id));
    }

    // ===================== CREATE =====================

    /**
     * Crear un atributo (Color, Talle, etc.)
     * POST /api/attributes
     */
    @PostMapping
    public ResponseEntity<AttributeResponse> create(
            @Valid @RequestBody CreateAttributeRequest request
    ) {
        AttributeResponse created = attributeService.createAttribute(request);

        URI location = URI.create("/api/attributes/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Agregar un valor a un atributo
     * POST /api/attributes/{id}/values
     */
    @PostMapping("/{attributeId}/values")
    public ResponseEntity<AttributeValueResponse> createValue(
            @PathVariable Long attributeId,
            @Valid @RequestBody CreateAttributeValueRequest request) {

        // ✅ Debe devolver AttributeValueResponse
        AttributeValueResponse response = attributeService.addValue(attributeId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===================== DELETE =====================

    /**
     * Eliminar atributo completo
     * DELETE /api/attributes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== UPDATE =====================

    /**
     * Actualizar nombre de atributo (ej: "Talle" -> "Talla")
     * PUT /api/attributes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttributeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAttributeRequest request) {
        return ResponseEntity.ok(attributeService.updateAttribute(id, request));
    }

    /**
     * Actualizar un valor específico (ej: cambiar Hex del color)
     * PUT /api/attributes/values/{valueId}
     * Nota: Usamos una ruta distinta porque valueId es único globalmente
     */
    @PutMapping("/values/{valueId}")
    public ResponseEntity<AttributeValueResponse> updateValue(
            @PathVariable Long valueId,
            @Valid @RequestBody UpdateAttributeValueRequest request) {
        return ResponseEntity.ok(attributeService.updateValue(valueId, request));
    }

    /**
     * Eliminar un valor específico (ej: dejar de vender XXL)
     * DELETE /api/attributes/values/{valueId}
     */
    @DeleteMapping("/values/{valueId}")
    public ResponseEntity<Void> deleteValue(@PathVariable Long valueId) {
        attributeService.deleteValue(valueId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderAttributes(@RequestBody ReorderIds request) {
        attributeService.reorderAttributes(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reordenar los valores dentro de un atributo (ej: S, M, L, XL)
     * PATCH /api/attributes/{id}/values/reorder
     */
    @PatchMapping("/{attributeId}/values/reorder")
    public ResponseEntity<Void> reorderValues(
            @PathVariable Long attributeId,
            @RequestBody ReorderIds request) {
        attributeService.reorderValues(attributeId, request);
        return ResponseEntity.noContent().build();
    }
}
