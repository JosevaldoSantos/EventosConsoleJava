package com.example.events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class SistemaEventos {
    private final Scanner in = new Scanner(System.in);
    private final List<Evento> eventos = new ArrayList<>();
    private Usuario usuario;

    public void executar() {
        // carregar dados
        eventos.addAll(Persistencia.carregarEventos());
        usuario = Optional.ofNullable(Persistencia.carregarUsuario()).orElse(null);

        System.out.println("==== Sistema de Eventos (Console) ====");
        boolean rodando = true;
        while (rodando) {
            try {
                mostrarMenu();
                int op = lerInt("Escolha uma opção: ");
                switch (op) {
                    case 1 -> cadastrarUsuario();
                    case 2 -> cadastrarEvento();
                    case 3 -> listarEventosOrdenados();
                    case 4 -> confirmarParticipacao();
                    case 5 -> cancelarParticipacao();
                    case 6 -> listarConfirmados();
                    case 7 -> salvar();
                    case 0 -> { salvar(); rodando = false; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.println("Encerrado.");
    }

    private void mostrarMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1) Cadastrar/Editar Usuário");
        System.out.println("2) Cadastrar Evento");
        System.out.println("3) Listar Eventos (ordenados por data/hora + status)");
        System.out.println("4) Confirmar participação em um Evento");
        System.out.println("5) Cancelar participação");
        System.out.println("6) Meus Eventos confirmados");
        System.out.println("7) Salvar agora");
        System.out.println("0) Sair");
    }

    private void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuário ---");
        String nome = lerStr("Nome: ");
        String email = lerStr("E-mail: ");
        String telefone = lerStr("Telefone: ");
        if (usuario == null) usuario = new Usuario(nome, email, telefone);
        else { usuario.setNome(nome); usuario.setEmail(email); usuario.setTelefone(telefone); }
        Persistencia.salvarUsuario(usuario);
        System.out.println("Usuário salvo.");
    }

    private void cadastrarEvento() {
        System.out.println("\n--- Cadastro de Evento ---");
        String nome = lerStr("Nome do evento: ");
        String endereco = lerStr("Endereço: ");
        Categoria.listarOpcoes();
        String catStr = lerStr("Categoria (digite uma das opções acima): ");
        Categoria cat = Categoria.fromString(catStr);
        LocalDateTime horario = lerDataHora("Data e hora (formato yyyy-MM-dd HH:mm): ");
        String desc = lerStr("Descrição: ");
        Evento ev = new Evento(null, nome, endereco, cat, horario, desc);
        eventos.add(ev);
        System.out.println("Evento cadastrado: " + ev);
    }

    private void listarEventosOrdenados() {
        System.out.println("\n--- Eventos ---");
        if (eventos.isEmpty()) {
            System.out.println("(nenhum evento cadastrado)"); return;
        }
        LocalDateTime agora = LocalDateTime.now();
        List<Evento> ordenados = eventos.stream()
                .sorted(Comparator.comparing(Evento::getHorario, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        for (int i = 0; i < ordenados.size(); i++) {
            Evento e = ordenados.get(i);
            String status = e.status(agora);
            System.out.printf("%d) %s | status: %s | id:%s\n", i+1, e.toString(), status, e.getId().toString().substring(0,8));
        }
        System.out.println("\nDica: use a opção 4 para confirmar presença usando o índice da lista.");
    }

    private void confirmarParticipacao() {
        if (usuario == null) {
            System.out.println("Cadastre um usuário primeiro (opção 1).");
            return;
        }
        if (eventos.isEmpty()) {
            System.out.println("Não há eventos para participar.");
            return;
        }
        listarEventosOrdenados();
        int idx = lerInt("Digite o número do evento para confirmar: ") - 1;
        if (idx < 0 || idx >= eventos.size()) {
            System.out.println("Índice inválido.");
            return;
        }
        Evento e = eventos.stream()
            .sorted(Comparator.comparing(Evento::getHorario, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()).get(idx);
        boolean added = usuario.confirmar(e.getId());
        if (added) {
            Persistencia.salvarUsuario(usuario);
            System.out.println("Presença confirmada em: " + e.getNome());
        } else {
            System.out.println("Você já havia confirmado presença neste evento.");
        }
    }

    private void cancelarParticipacao() {
        if (usuario == null) {
            System.out.println("Cadastre um usuário primeiro (opção 1).");
            return;
        }
        if (usuario.getEventosConfirmados().isEmpty()) {
            System.out.println("Você não tem confirmações ainda.");
            return;
        }
        listarConfirmados();
        String idCurto = lerStr("Digite o ID curto (8 primeiros caracteres) do evento para cancelar: ").trim();
        UUID escolhido = null;
        for (UUID id : usuario.getEventosConfirmados()) {
            if (id.toString().substring(0,8).equalsIgnoreCase(idCurto)) {
                escolhido = id; break;
            }
        }
        if (escolhido == null) {
            System.out.println("ID não encontrado entre seus eventos confirmados.");
            return;
        }
        boolean rem = usuario.cancelar(escolhido);
        if (rem) {
            Persistencia.salvarUsuario(usuario);
            System.out.println("Participação cancelada.");
        } else {
            System.out.println("Não foi possível cancelar (não estava confirmado?).");
        }
    }

    private void listarConfirmados() {
        System.out.println("\n--- Meus eventos confirmados ---");
        if (usuario == null) { System.out.println("Cadastre um usuário (opção 1)." ); return; }
        if (usuario.getEventosConfirmados().isEmpty()) { System.out.println("(vazio)"); return; }
        LocalDateTime agora = LocalDateTime.now();
        for (UUID id : usuario.getEventosConfirmados()) {
            eventos.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .ifPresent(e -> System.out.printf("- %s | %s | %s | id:%s\n",
                        e.getNome(),
                        e.getHorario()==null?"(sem horário)":e.getHorario().format(Evento.FMT),
                        e.status(agora),
                        e.getId().toString().substring(0,8)));
        }
    }

    private void salvar() {
        Persistencia.salvarEventos(eventos);
        Persistencia.salvarUsuario(usuario);
        System.out.println("Dados salvos (events.data e user.data).");
    }

    // utilitários
    private int lerInt(String prompt) {
        System.out.print(prompt);
        while (!in.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            in.next();
        }
        int v = in.nextInt();
        in.nextLine(); // consumir quebra de linha
        return v;
    }

    private String lerStr(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    private LocalDateTime lerDataHora(String prompt) {
        System.out.print(prompt);
        String s = in.nextLine();
        try {
            return LocalDateTime.parse(s, Evento.FMT);
        } catch (Exception e) {
            System.out.println("Formato inválido. Exemplo válido: 2025-09-01 19:30");
            return null;
        }
    }
}
