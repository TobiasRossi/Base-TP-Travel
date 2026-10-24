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
    │   │               │   │   Reserva.java                 (Context del State, Subject del Observer)
    │   │               │   │   OperacionesReserva.java
    │   │               │   │   RegistroReservas.java
    │   │               │   │   Cobro.java
    │   │               │   │   VoucherCredito.java
    │   │               │   │   EstadoReserva.java           (State; los estados concretos van en el
    │   │               │   │   SolicitudPendiente.java       mismo paquete para que sus operaciones
    │   │               │   │   Confirmada.java               sean package-private)
    │   │               │   │   EnCheckIn.java
    │   │               │   │   EnCurso.java
    │   │               │   │   Finalizada.java
    │   │               │   │   Cancelada.java
    │   │               │   │   NombreEstado.java
    │   │               │   │   ReservaException.java        (+ OperacionInvalida, ReservaVacia,
    │   │               │   │                                   ItemInvalido, ItemNoIncluido,
    │   │               │   │                                   CantidadPasajerosInvalida)
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

## Dependencias del 2.2

El módulo 2.2 usa estas clases y métodos de otros módulos. Hasta que cada módulo los agregue, el 2.2 no compila:

| Módulo | Qué necesita el 2.2 | Para qué |
|--------|---------------------|----------|
| 2.1 | `ItemCatalogo.liberar(Periodo fechas, Integer cantidad)` | Liberar un cupo por pasajero al cancelar o si falla el pago (el bloqueo ya existe: `reservar(Periodo, Integer)`) |
| 2.3 | `MetodoTraslado.calcularCosto(Reserva reserva): double` | Calcular las tasas del traslado elegido |
| 2.3 | `MetodoTraslado.verificarDisponibilidad(Reserva reserva)` | Verificar el traslado elegido antes de bloquear cupos y cobrar |
| 2.3 | `TrasladoNoDisponibleException` (paquete `traslado`) | La lanza `verificarDisponibilidad` si no hay disponibilidad |
| 2.4 | `MetodoPago` con `ComprobantePago procesarPago(double monto)` (paquete `procesadorpago`) | Cobrar la reserva al confirmar |
| 2.4 | `ComprobantePago` con `getCodigoTransaccion(): String` y `getMonto(): double` | Guardarlo en el `Cobro` |
| 2.4 | `PagoRechazadoException` | Saber que el pago falló y liberar los cupos |

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
| 2.2 | Patrón **State**: `Reserva` (Context) delega cada operación en `EstadoReserva` (State); cada estado concreto (ConcreteState) redefine solo las operaciones que le son válidas y el resto lanza `OperacionInvalidaException` | Agregar o cambiar un estado no obliga a tocar las demás operaciones; no hay `if`/`switch` sobre el estado | Un `enum` de estados con `switch` en cada operación de `Reserva` |
| 2.2 | Los estados viven en el mismo paquete que `Reserva` y sus operaciones (y los efectos de `Reserva` que invocan) son package-private | Desde afuera solo se puede usar `OperacionesReserva`: nadie puede forzar una transición ni ejecutar un efecto fuera de lugar | Subpaquete `estado` con todo público, confiando en que nadie lo use mal |
| 2.2 | La Reserva sabe **hacer** (bloquear cupos, cobrar, generar vouchers); el Estado decide **cuándo** | Cada estado se lee como la regla del enunciado y la lógica no se duplica entre estados | Que cada estado manipule directamente las colecciones de la Reserva |
| 2.2 | Pago dentro de `confirmar(MetodoPago)`; orden: ítems → traslado → cupos → cobro → transición | La reserva no queda pagada sin cupo ni con cupos bloqueados sin pago; si algo falla, se deshace y no se notifica | Cobrar primero y devolver la plata si no hay cupo |
| 2.2 | Bloqueo de cupos todo-o-nada, una unidad por pasajero | Si un ítem no tiene cupo, se liberan los ya bloqueados; una reserva de N pasajeros consume y paga N unidades | Bloquear ítem por ítem sin rollback; una unidad por ítem sin importar los pasajeros |
| 2.2 | Cada línea de la reserva es un `ItemReservado(item, periodo)`: `agregarItem(item, periodo)` | El ítem es uno solo en el catálogo; lo que se reserva es "ese ítem en esas fechas". La cantidad de pasajeros es de toda la reserva, no de cada línea | Guardar la fecha en el ítem (es de todas las reservas) o en la reserva (no permite ítems en fechas distintas) |
| 2.2 | `Cobro` inmutable (montos, comprobante, fecha con `Clock` inyectable) | Reembolsos, factura y reportes usan lo que efectivamente se pagó y cuándo, aunque después cambien los precios | Recalcular con precios actuales al cancelar |
| 2.2 | El traslado es un `Optional<MetodoTraslado>`: vacío al crear la reserva, con valor en `elegirTraslado` | Sin traslado elegido las tasas son 0 y no se verifica disponibilidad, sin `null` ni `if`; no se inventa un método de traslado que el enunciado no menciona | `metodoTraslado == null ? 0 : …`, o una clase `SinTraslado` (Null Object) ajena al 2.3 |
| 2.2 | El 2.2 solo implementa sus propias clases; lo que necesita de otros módulos lo usa por su nombre y queda sin compilar hasta que ese módulo lo agregue (ver "Dependencias del 2.2") | Cada módulo es dueño de su código y el historial refleja quién hizo qué | Que el 2.2 cree clases y métodos de otros módulos para compilar |
| 2.2 | Excepciones de dominio propias bajo `ReservaException` (`OperacionInvalida`, `ReservaVacia`, `ItemInvalido`, `ItemNoIncluido`, `CantidadPasajerosInvalida`) | El que llama puede distinguir y atrapar errores del dominio; el mensaje explica qué regla se violó | `RuntimeException` / `IllegalStateException` genéricas o devolver `null` |
| 2.2/2.5 | Patrón **Observer**: `Reserva` (Subject) notifica a cada `ReservaObserver` con estado anterior y nuevo; un observer por subsistema; `RegistroReservas` los suscribe a cada reserva | Agregar subsistemas sin tocar `Reserva` ni a los demás (OCP); la Reserva no los conoce | Un único observer que hace todo, o que `Reserva` llame directamente a mail, factura y club |
