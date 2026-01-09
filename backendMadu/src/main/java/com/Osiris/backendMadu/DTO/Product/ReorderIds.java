package com.Osiris.backendMadu.DTO.Product;

import java.util.List;

public record ReorderIds(
        List<Long> orderedIds
) {
}