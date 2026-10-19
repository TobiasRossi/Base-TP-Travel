# CLAUDE.md — UNQ-Travel

Instrucciones para Claude Code en este repositorio. Leelas antes de cualquier tarea.

## Qué es este proyecto

TP Integrador de Programación Orientada a Objetos (UNQ). Capa de **dominio** en Java de un sistema de reservas de viajes. Ver `README.md` para el alcance completo y los módulos (2.1 a 2.8). El enunciado de cada módulo está en su Issue de GitHub.

Es un trabajo **académico y grupal**: los integrantes tienen que poder explicar y defender cada línea en una entrevista individual con el IDE abierto.

## Reglas del dominio

- Solo capa de dominio: **sin UI, sin persistencia, sin frameworks**. Solo clases, interfaces y tests.
- Sin dependencias nuevas en `pom.xml` salvo que se pida explícitamente. Producción: solo la JDK. Tests: JUnit y Mockito.
- Cada decisión de diseño tiene que poder justificarse: qué problema resuelve y qué alternativa más frágil evita.
- Cuando uses un patrón de diseño, nombralo y explicá los roles según Gamma et al. (por ejemplo, en un Composite: Component, Leaf, Composite).

## Stack y comandos

- Java 26, Maven, JUnit (Jupiter), Mockito, JaCoCo.
- Correr tests: `mvn test`
- Cobertura + chequeo del mínimo: `mvn verify` (reporte en `target/site/jacoco/index.html`)
- Objetivo: **95 % de cobertura de líneas** del código de dominio. `mvn verify` falla si no se llega.

## Convenciones de código

- Nombres de clases, métodos y variables en **español**, siguiendo el vocabulario del enunciado (Reserva, Paquete, MetodoTraslado...).
- Paquete base: `com.pepito.reservas` (provisorio, ver README), un subpaquete por módulo.
- Los estados inválidos se señalan con excepciones de dominio propias, no con `RuntimeException` genéricas ni con `null`.
- Preferir polimorfismo a `if`/`switch` sobre tipos o estados.
- Cada clase de dominio tiene su test en `src/test/java`, con el mismo paquete. Un test por comportamiento, con nombre descriptivo.

## Cómo trabajar

- Antes de implementar un módulo, proponé un **plan** (clases, responsabilidades, patrón y por qué) y esperá aprobación.
- Cambios chicos y enfocados en un solo módulo. No toques otros módulos sin avisar.
- Después de cambiar código, corré `mvn test` y reportá el resultado.
- Al terminar una decisión de diseño importante, proponé la fila para la tabla "Decisiones de diseño" del README (o el archivo correspondiente en `docs/`).
- Explicá el razonamiento de cada decisión: el objetivo es que el integrante aprenda y pueda defenderlo, no solo entregar código.

## Git

- Una rama por cambio, integración por Pull Request.
- No hagas commits ni push sin que te lo pidan: la participación se evalúa por el historial de cada integrante.
