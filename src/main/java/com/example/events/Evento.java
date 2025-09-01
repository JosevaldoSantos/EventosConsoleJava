package com.example.events;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class Evento {
    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final long DURACAO_PADRAO_MIN = 120; // suposição: 2h

    private UUID id;
    private String nome;
    private String endereco;
    private Categoria categoria;
    private LocalDateTime horario;
    private String descricao;

    public Evento(UUID id, String nome, String endereco, Categoria categoria, LocalDateTime horario, String descricao) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public Categoria getCategoria() { return categoria; }
    public LocalDateTime getHorario() { return horario; }
    public String getDescricao() { return descricao; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String status(LocalDateTime agora) {
        if (agora == null) agora = LocalDateTime.now();
        if (horario == null) return "SEM HORÁRIO";
        long diffMin = Duration.between(horario, agora).toMinutes();
        if (Math.abs(diffMin) <= DURACAO_PADRAO_MIN) return "OCORRENDO AGORA";
        if (horario.isBefore(agora)) return "JÁ OCORREU";
        return "EM BREVE";
    }

    public String toCsv() {
        // id;nome;endereco;categoria;horario;descricao
        String safeDesc = descricao == null ? "" : descricao.replace("\n", "\\n");
        String safeNome = nome == null ? "" : nome.replace(";", ",");
        String safeEndereco = endereco == null ? "" : endereco.replace(";", ",");
        String safeCategoria = categoria == null ? "OUTROS" : categoria.name();
        String safeHorario = horario == null ? "" : horario.format(FMT);
        return String.join(";", id.toString(), safeNome, safeEndereco, safeCategoria, safeHorario, safeDesc);
    }

    public static Evento fromCsv(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(";", -1);
        if (p.length < 6) return null;
        UUID id = UUID.fromString(p[0]);
        String nome = p[1];
        String endereco = p[2];
        Categoria categoria = Categoria.fromString(p[3]);
        LocalDateTime horario = p[4].isBlank() ? null : LocalDateTime.parse(p[4], FMT);
        String descricao = p[5].replace("\\n", "\n");
        return new Evento(id, nome, endereco, categoria, horario, descricao);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %s | %s",
            id.toString().substring(0, 8),
            nome,
            categoria,
            horario == null ? "(sem horário)" : horario.format(FMT),
            endereco,
            descricao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
