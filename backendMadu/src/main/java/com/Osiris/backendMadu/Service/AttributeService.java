package com.Osiris.backendMadu.Service;

import com.Osiris.backendMadu.DTO.*;
import com.Osiris.backendMadu.Entity.Attribute;
import com.Osiris.backendMadu.Entity.AttributeType;
import com.Osiris.backendMadu.Entity.AttributeValue;
import com.Osiris.backendMadu.Mapper.AttributeMapper;
import com.Osiris.backendMadu.Repository.AttributeRepository;
import com.Osiris.backendMadu.Repository.AttributeValueRepository;
import com.Osiris.backendMadu.Utils.SlugGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeMapper attributeMapper;
    private final SlugGenerator slugGenerator;

    public List<AttributeResponse> findAll() {
        return attributeRepository.findAllOrderByPosition()
                .stream()
                .map(attributeMapper::toDto)
                .toList();
    }

    public AttributeResponse findById(Long id) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Attribute not found with id: " + id)
                );
        return attributeMapper.toDto(attribute);
    }

    @Transactional
    public AttributeResponse createAttribute(CreateAttributeRequest request) {


        String slug = slugGenerator.generateSlug(request.getName());
        if (attributeRepository.existsBySlug(slug)) {
            throw new IllegalStateException("Attribute with slug already exists: " + slug);
        }

        Attribute attribute = attributeMapper.toEntity(request);
        attribute.setSlug(slug);


        if (request.getPosition() == null) {
            Integer maxPos = attributeRepository.findMaxPosition();
            attribute.setPosition(maxPos != null ? maxPos + 1 : 1);
        }

        return attributeMapper.toDto(attributeRepository.save(attribute));
    }


    @Transactional
    public AttributeResponse updateAttribute(Long id, UpdateAttributeRequest request) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found"));

        Integer currentPos = attribute.getPosition();
        attributeMapper.updateEntityFromDto(request, attribute);


        if (attribute.getPosition() == null) {
            attribute.setPosition(currentPos != null ? currentPos : 0);
        }
        return attributeMapper.toDto(attributeRepository.save(attribute));
    }

    @Transactional
    public AttributeValueResponse updateValue(Long valueId, UpdateAttributeValueRequest request) {
        AttributeValue value = attributeValueRepository.findById(valueId)
                .orElseThrow(() -> new EntityNotFoundException("Attribute Value not found"));

        attributeMapper.updateValueFromDto(request, value);

        return attributeMapper.toValueDto(attributeValueRepository.save(value));
    }
    // ===================== ADD VALUE =====================

    @Transactional
    public AttributeValueResponse addValue(Long attributeId, CreateAttributeValueRequest request) {

        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Attribute not found")
                );


        if (attribute.getType() != AttributeType.SELECT && attribute.getType() != AttributeType.COLOR) {
            throw new IllegalStateException("Cannot add values to attribute of type " + attribute.getType());
        }

        if (attribute.getType() == AttributeType.COLOR) {
            if (!StringUtils.hasText(request.getHexColor())) {
                throw new IllegalArgumentException("El código hexadecimal (hexColor) es obligatorio para atributos de tipo COLOR.");
            }
        }

        String slugToCheck = StringUtils.hasText(request.getSlug())
                ? request.getSlug()
                : slugGenerator.generateSlug(request.getValue());

        boolean exists = attributeValueRepository.existsByAttributeIdAndSlug(attributeId, slugToCheck);
        if (exists) {
            throw new IllegalStateException("Value already exists: " + request.getValue());
        }

        AttributeValue value = attributeMapper.toValueEntity(request);
        value.setSlug(slugToCheck);
        value.setAttribute(attribute);


        if (request.getPosition() == null) {
            Integer maxPosition = attributeValueRepository.findMaxPositionByAttributeId(attributeId);
            value.setPosition(maxPosition != null ? maxPosition + 1 : 1);
        }

        return attributeMapper.toValueDto(attributeValueRepository.save(value));
    }

    @Transactional
    public void deleteAttribute(Long id) {
        if (!attributeRepository.existsById(id)) {
            throw new EntityNotFoundException("Attribute not found");
        }
        attributeRepository.deleteById(id);
    }

    @Transactional
    public void deleteValue(Long valueId) {
        if (!attributeValueRepository.existsById(valueId)) {
            throw new EntityNotFoundException("Attribute Value not found with id: " + valueId);
        }

        attributeValueRepository.deleteById(valueId);
    }


    @Transactional
    public void reorderValues(Long attributeId, ReorderIds request) {
        if (!attributeRepository.existsById(attributeId)) {
            throw new EntityNotFoundException("Attribute not found");
        }

        List<Long> orderedIds = request.orderedIds();

        for (int i = 0; i < orderedIds.size(); i++) {
            Long valueId = orderedIds.get(i);
            attributeValueRepository.updatePositionIfBelongsToAttribute(valueId, attributeId, i);
        }
    }

    @Transactional
    public void reorderAttributes(ReorderIds request) {
        List<Long> orderedIds = request.orderedIds();
        for (int i = 0; i < orderedIds.size(); i++) {
            Long attributeId = orderedIds.get(i);
            attributeRepository.updateAttributePosition(attributeId, i);
        }
    }

}
