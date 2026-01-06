package com.Osiris.backendMadu.Utils;

import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {

    public String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Cannot generate slug from empty text");
        }

        return text.toLowerCase()
                .replaceAll("[áàäâã]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöôõ]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("ñ", "n")
                .replaceAll("ç", "c")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");  // Evitar guiones múltiples
    }

    public String generateUniqueSlug(String text, int attempt) {
        String baseSlug = generateSlug(text);
        return attempt > 0 ? baseSlug + "-" + attempt : baseSlug;
    }
}