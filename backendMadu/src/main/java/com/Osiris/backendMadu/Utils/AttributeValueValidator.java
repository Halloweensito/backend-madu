package com.Osiris.backendMadu.Utils;

import com.Osiris.backendMadu.Entity.AttributeType;
import com.Osiris.backendMadu.Entity.AttributeValue;
import com.Osiris.backendMadu.Repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AttributeValueValidator {

    private final AttributeValueRepository attributeValueRepository;

    public List<AttributeValue> validateAndRetrieve(List<Long> attributeValueIds) {
        Set<Long> uniqueIds = new HashSet<>(attributeValueIds);
        var values = attributeValueRepository.findAllById(uniqueIds);

        if (values.size() != uniqueIds.size()) {
            throw new IllegalStateException("One or more AttributeValues not found");
        }

        validateSelectType(values);
        validateNoDuplicateAttributes(values);

        return values;
    }

    private void validateSelectType(List<AttributeValue> values) {
        boolean hasNonSelectAttribute = values.stream()
                .anyMatch(v -> v.getAttribute().getType() != AttributeType.SELECT);

        if (hasNonSelectAttribute) {
            throw new IllegalStateException("All attributes must be SELECT type");
        }
    }

    private void validateNoDuplicateAttributes(List<AttributeValue> values) {
        long uniqueCount = values.stream()
                .map(v -> v.getAttribute().getId())
                .distinct()
                .count();

        if (uniqueCount != values.size()) {
            throw new IllegalStateException("Duplicate attributes detected");
        }
    }
}