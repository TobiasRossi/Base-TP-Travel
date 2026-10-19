# UNQ-Travel — Sistema de Reservas

Trabajo Práctico Integrador de Programación Orientada a Objetos.

Diseño e implementación en **Java** de la **capa de dominio** de un sistema de reservas para una empresa de viajes y turismo online: catálogo de servicios y paquetes, reservas, traslados y tasas, pagos, notificaciones, búsqueda y reportes.

> **Estado del proyecto:** en etapa de diseño. Todavía no hay código.

## Alcance

El trabajo consiste en diseñar e implementar **únicamente la capa de dominio**: sin interfaz gráfica, sin persistencia y sin frameworks. Solo clases, interfaces y pruebas.

El objetivo no es construir una aplicación completa, sino demostrar la correcta aplicación de los principios de diseño orientado a objetos en un dominio realista. Cada decisión de diseño debe poder justificarse en términos de qué problema resuelve y qué alternativa más frágil evita.

## Integrantes

| Nombre y apellido | Email | Usuario de GitHub |
|-------------------|-------|-------------------|
| _Dominguez, Tomás_       | _tomaselias.dw@gmail.com_ | _TomasDominguez10_ |
| _Rossi, Tobías_       | _trossi2001@gmail.com_ | _TobiasRossi_ |
| _Vallejos, Daniel_       | _davallejos_ps@outlook.com_ | _dcvallejos_ |

## Módulos del dominio

Cada módulo tiene su propio Issue en este repositorio con el enunciado completo.

| Módulo | Descripción |
|--------|-------------|
| 2.1 Catálogo de servicios y paquetes | Servicios individuales y paquetes (que pueden contener otros paquetes), con atributos dinámicos y validación. |
| 2.2 Ciclo de vida de la reserva | Estados de la reserva, operaciones válidas por estado y excepción de dominio propia. |
| 2.3 Métodos de traslado y tasas | Traslado estándar, VIP / Chauffeur y punto de encuentro. |
| 2.4 Métodos de pago | Proceso de pago de pasos fijos con variantes por medio de pago (tarjeta, transferencia, billetera/crypto). |
| 2.5 Notificaciones de la reserva | Subsistemas que reaccionan a los cambios de estado sin que el módulo de reservas los conozca. |
| 2.6 Búsqueda en el catálogo | Criterios simples combinables con AND, OR y NOT, anidables. |
| 2.8 Reportes | Reportes exportables en texto plano, CSV y HTML. |

## Entregables

- [ ] Diagrama de clases UML de la solución completa.
- [ ] Documentación en PDF con integrantes y emails, decisiones de diseño, detalles de implementación, patrones utilizados y sus roles según Gamma et al.
- [ ] Implementación completa en Java con tests de unidad y **95 % de cobertura** del código de dominio.
- [ ] Repositorio accesible para los docentes, con historial de trabajo equitativo entre integrantes.

## Fechas de entrega

| Instancia | Fecha |
|-----------|-------|
| 1.ª fecha | 12 de noviembre, en el horario de práctica |
| 2.ª fecha | 19 de noviembre de 2026, en el horario de práctica |

Después de cada entrega hay una corrección *off-line* y una entrevista individual y remota con cada integrante. En la entrevista hay que tener el IDE abierto con el TP ejecutable.

## Estructura del repositorio

_A definir cuando se cree el proyecto._ Propuesta inicial:

```
.
C:.
│   pom.xml
│   README.md
│
├───.mvn
└───src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───pepito
    │   │           └───reservas
    │   │               │   Main.java
    │   │               │
    │   │               ├───catalogo
    │   │               │   │   Catalogo.java
    │   │               │   │   ItemCatalogo.java
    │   │               │   │
    │   │               │   └───busqueda
    │   │               │           CriterioBusqueda.java
    │   │               │
    │   │               ├───cliente
    │   │               │       Cliente.java
    │   │               │
    │   │               ├───notificador
    │   │               ├───procesadorpago
    │   │               ├───reporte
    │   │               ├───reserva
    │   │               │   │   GestorReservas.java
    │   │               │   │   Reserva.java
    │   │               │   │   VoucherCredito.java
    │   │               │   │
    │   │               │   ├───estado
    │   │               │   │       Cancelada.java
    │   │               │   │       CheckIn.java
    │   │               │   │       Confirmada.java
    │   │               │   │       EnCurso.java
    │   │               │   │       EstadoReserva.java
    │   │               │   │       Finalizada.java
    │   │               │   │       MetodoInvalidoException.java
    │   │               │   │       NombreEstado.java
    │   │               │   │       SolicitudPendiente.java
    │   │               │   │
    │   │               │   └───suscriptor
    │   │               │           ReservaObserver.java
    │   │               │
    │   │               ├───servicio
    │   │               │       GestorServicios.java
    │   │               │       Paquete.java
    │   │               │       Servicio.java
    │   │               │       Validador.java
    │   │               │
    │   │               └───traslado
    │   │                       MetodoTraslado.java
    │   │
    │   └───resources
    └───test
        └───java
```

## Tecnologías

- Lenguaje: Java _(versión a definir)_
- Build: _(Maven / Gradle, a definir)_
- Tests: _(JUnit, a definir)_
- Cobertura: _(JaCoCo u otra, a definir)_

## Cómo trabajamos

- Un Issue por módulo del dominio; el trabajo se organiza a partir de ellos.
- Cada cambio se hace en una rama propia y se integra con Pull Request.
- Todos los integrantes commitean con su propio usuario: la participación se evalúa por el historial de git.
- Las decisiones de diseño importantes se anotan en `docs/` (qué problema resuelve y qué alternativa se descartó), porque hay que justificarlas en la entrevista.

## Decisiones de diseño

_Se va completando a medida que avanza el diseño._

| Módulo | Decisión | Problema que resuelve | Alternativa descartada |
|--------|----------|-----------------------|------------------------|
| _—_ | _—_ | _—_ | _—_ |
