package com.proply.shared.util;

import java.text.Normalizer;

public class SlugUtil {

    public static String generate(String input) {

        if (input == null || input.isBlank()) {
            return "";
        }

        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        slug = slug
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug;
    }
}