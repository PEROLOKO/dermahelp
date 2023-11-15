package com.dermahelp.exceptions;

public record RestError(
    int cod,
    String message
) {}
