package com.example.events;

public enum Categoria {
    FESTA,
    SHOW,
    ESPORTE,
    PALESTRA,
    TEATRO,
    FEIRA,
    OUTROS;

    public static Categoria fromString(String s) {
        if (s == null) return OUTROS;
        try {
            return Categoria.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return OUTROS;
        }
    }

    public static void listarOpcoes() {
        System.out.println("Categorias disponíveis:");
        for (Categoria c : values()) {
            System.out.println(" - " + c.name());
        }
    }
}
