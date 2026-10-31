package com.orlandini.academiawebflux.exception;
import java.time.LocalDateTime;
public record CustomErrorResponse(LocalDateTime datetime, String message) { }
