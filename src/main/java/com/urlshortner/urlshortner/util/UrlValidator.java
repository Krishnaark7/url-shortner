package com.urlshortner.urlshortner.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator {

    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String trimmed = input.trim();

        String lower = trimmed.toLowerCase();
        if (lower.startsWith("javascript:") || lower.startsWith("data:") || lower.startsWith("file:")) {
            return false;
        }

        try {
            URI uri = new URI(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return false;
            }
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                return false;
            }
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }
}