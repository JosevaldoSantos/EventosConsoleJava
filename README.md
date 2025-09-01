# Sistema de Eventos (Console, Java)

Projeto didático em Java para cadastro, listagem e confirmação de participação em eventos, com persistência em arquivo `events.data`.

## Requisitos
- Java 11+ (JDK)

## Compilar e Executar
```bash
cd EventosConsoleJava/src/main/java
javac com/example/events/*.java
java com.example.events.App
```

## Observações
- Os dados de **eventos** são salvos no arquivo `events.data` (exigência da atividade).
- Os dados do **usuário** e das **confirmações** são salvos em `user.data` (extra opcional para sua conveniência).
- Datas no formato: `yyyy-MM-dd HH:mm` (ex.: `2025-09-15 19:30`).
- Status do evento: 
  - **OCORRENDO AGORA**: quando a hora atual está a até 2h da hora do evento (suposição didática).
  - **JÁ OCORREU**: quando a data/hora do evento é anterior ao momento atual.
  - **EM BREVE**: quando a data/hora do evento é futura.
