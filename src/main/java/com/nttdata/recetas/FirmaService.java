package com.nttdata.recetas;

import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calcula la firma de una receta. (RN-04)
 * La clave no se guarda aqui: llega como parametro desde el recurso,
 * que a su vez la recibe de HashiCorp Vault.
 */
@ApplicationScoped
public class FirmaService {

    // TODO 13: declara una constante privada con la longitud de la firma (16).
    private static final int LONGITUD_FIRMA = 16;

    // TODO 14 (RN-04): calcula el SHA-256 del texto  codigoReceta + ":" + clave
    //          Conviertelo a hexadecimal en MINUSCULAS y devuelve los primeros 16 caracteres.
    //          Pista: String.format("%02x", b) convierte un byte en hexadecimal.
    public String firmar(String codigoReceta, String clave) {
        return toHex(sha256(codigoReceta + ":" + clave)).substring(0, LONGITUD_FIRMA);
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /** Utilidad: devuelve el resumen SHA-256 del texto. (Resuelto) */
    private byte[] sha256(String texto) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
