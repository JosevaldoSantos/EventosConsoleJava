package com.example.events;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Usuario {
    private String nome;
    private String email;
    private String telefone;
    private final Set<UUID> eventosConfirmados = new HashSet<>();

    public Usuario(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Set<UUID> getEventosConfirmados() { return eventosConfirmados; }

    public boolean confirmar(UUID eventoId) {
        return eventosConfirmados.add(eventoId);
    }

    public boolean cancelar(UUID eventoId) {
        return eventosConfirmados.remove(eventoId);
    }
}
