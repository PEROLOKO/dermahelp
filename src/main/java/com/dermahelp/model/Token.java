package com.dermahelp.model;

public record Token(
        String token,
        String type,
        String prefix
) {
}
