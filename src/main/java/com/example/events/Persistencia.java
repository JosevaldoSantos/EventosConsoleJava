package com.example.events;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Persistencia {
    private static final String EVENTS_FILE = "events.data"; // exigido
    private static final String USER_FILE = "user.data";     // extra (opcional)

    public static List<Evento> carregarEventos() {
        List<Evento> eventos = new ArrayList<>();
        File f = new File(EVENTS_FILE);
        if (!f.exists()) return eventos;
        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                Evento ev = Evento.fromCsv(line);
                if (ev != null) eventos.add(ev);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar eventos: " + e.getMessage());
        }
        return eventos;
    }

    public static void salvarEventos(List<Evento> eventos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENTS_FILE, StandardCharsets.UTF_8, false))) {
            for (Evento e : eventos) {
                bw.write(e.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    public static void salvarUsuario(Usuario u) {
        if (u == null) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE, StandardCharsets.UTF_8, false))) {
            // primeira linha: nome;email;telefone
            bw.write(String.join(";", safe(u.getNome()), safe(u.getEmail()), safe(u.getTelefone())));
            bw.newLine();
            // segunda linha: lista de UUIDs confirmados separados por ;
            if (!u.getEventosConfirmados().isEmpty()) {
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for (UUID id : u.getEventosConfirmados()) {
                    if (!first) sb.append(";"); else first = false;
                    sb.append(id.toString());
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public static Usuario carregarUsuario() {
        File f = new File(USER_FILE);
        if (!f.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String first = br.readLine();
            if (first == null || first.isBlank()) return null;
            String[] p = first.split(";", -1);
            String nome = p.length > 0 ? p[0] : "";
            String email = p.length > 1 ? p[1] : "";
            String telefone = p.length > 2 ? p[2] : "";
            Usuario u = new Usuario(nome, email, telefone);
            String second = br.readLine();
            if (second != null && !second.isBlank()) {
                String[] ids = second.split(";");
                for (String s : ids) {
                    try { u.getEventosConfirmados().add(UUID.fromString(s)); } catch (Exception ignore) {}
                }
            }
            return u;
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuário: " + e.getMessage());
            return null;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
