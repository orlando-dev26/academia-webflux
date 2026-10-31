package com.orlandini.academiawebflux.exception;
public final class ErrorMessages {
    private ErrorMessages() { }
    public static String forStatus(int status) {
        return switch (status) {
            case 400, 422 -> "Solicitud invalida o datos incompletos";
            case 401 -> "Autenticacion requerida o credenciales invalidas";
            case 403 -> "Acceso denegado";
            case 404 -> "Recurso no encontrado";
            case 405 -> "Metodo no permitido";
            case 415 -> "Tipo de contenido no admitido";
            default -> status >= 500 ? "Error interno del servidor" : "No se pudo procesar la solicitud";
        };
    }
}
