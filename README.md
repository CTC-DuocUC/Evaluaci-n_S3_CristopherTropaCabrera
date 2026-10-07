# SpeedFast – Semana 8: CRUD con patrón DAO, JDBC y Swing

Actividad sumativa individual – Desarrollo Orientado a Objetos II

Aplicación de escritorio que gestiona **repartidores, pedidos y entregas** de SpeedFast con persistencia en MySQL.

## Estructura del proyecto

```
SpeedFast-Semana8/
├── bd/
│   ├── speedfast_db.sql          (base de datos y tablas)
│   └── datos_ejemplo.sql         (datos opcionales para probar)
├── lib/
│   └── mysql-connector-j-26.7.0.jar
└── src/
    ├── main/     Main
    ├── modelo/   Repartidor, Pedido, Entrega, TipoPedido, EstadoPedido
    ├── dao/      ConexionDB, RepartidorDAO, PedidoDAO, EntregaDAO
    │   └── impl/ RepartidorDAOImpl, PedidoDAOImpl, EntregaDAOImpl
    ├── vista/    VentanaPrincipal, PanelRepartidores, PanelPedidos, PanelEntregas, Refrescable
    └── util/     Validador, Mensajes, Interfaz
```

## Cómo ejecutarlo

1. **Base de datos:** en MySQL Workbench abre y ejecuta `bd/speedfast_db.sql`. Opcionalmente ejecuta después `bd/datos_ejemplo.sql`.
2. **Contraseña:** abre `src/dao/ConexionDB.java` y reemplaza `tu_contraseña` por la clave de tu MySQL. No subas tu clave real a GitHub.
3. **IntelliJ IDEA:** crea un proyecto Java, copia dentro las carpetas `src`, `lib` y `bd`, y agrega el conector: *File > Project Structure > Libraries > + > Java* y elige `lib/mysql-connector-j-26.7.0.jar`.
4. Ejecuta `src/main/Main.java`.

Por consola, desde la raíz del proyecto:

```bash
javac -encoding UTF-8 -cp lib/mysql-connector-j-26.7.0.jar -d out $(find src -name "*.java")
java -cp out:lib/mysql-connector-j-26.7.0.jar main.Main
```

En Windows se usa `;` en lugar de `:` para separar el classpath.

## Cumplimiento de la rúbrica

| Criterio | Dónde se cumple                                                                                                                                                |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1. DAO con CRUD completo, `PreparedStatement` y `ResultSet` | `dao/` (interfaces) y `dao/impl/`: cada DAO tiene `create()`, `readAll()`, `update()` y `delete()`. Los filtros opcionales son sobrecargas de `readAll`        |
| 2. Interfaz conectada a los DAO | Los tres paneles de `vista/` invocan los DAO y refrescan su `JTable` después de cada operación. Al cambiar de pestaña se recargan los datos                    |
| 3. Validación de entradas | `util/Validador`: campos obligatorios, largo máximo de 100, formato de nombre, fecha (DD-MM-AAAA) y hora (HH:mm), y selección en combos                        |
| 4. Manejo de errores SQL | Cada operación de los paneles captura `SQLException` y llama a `Mensajes.errorSQL`, que muestra un `JOptionPane` claro y deja el detalle técnico en la consola |
| 5. Interfaz Swing funcional | `JFrame`, `JTabbedPane`, `JPanel`, `JTable`, `JTextField`, `JComboBox`, `JButton` y `JOptionPane`                                                              |
| 6. Buenas prácticas | Separación en capas (modelo, dao, vista, util), interfaces DAO con su implementación, utilidades reutilizadas y comentarios breves en los métodos clave        |
| 7. GitHub | Repositorio público con commits ordenados por etapa                                                                                                            |

## Detalles de diseño

- **Combos con id oculto:** `Pedido` y `Repartidor` sobrescriben `toString()` para mostrar `id - texto`, pero el combo guarda el objeto completo, así que el id viaja dentro.
- **Cierre de recursos:** `Connection`, `PreparedStatement` y `ResultSet` se abren con try-with-resources, que los cierra solos incluso si ocurre un error.
- **Claves foráneas:** si se intenta eliminar un pedido o repartidor que ya tiene entregas, MySQL lo rechaza y la aplicación explica el motivo.
- **Entregas:** `readAll` une con `pedidos` y `repartidores` (`LEFT JOIN`) para mostrar la dirección y el nombre en la tabla en vez de solo los números.

## Notas sobre la pauta

- El Paso 2 menciona `ClienteDAO`. Según la aclaración del profesor, la clase correcta es **`RepartidorDAO`**.
- El script de la Semana 7 crea la base `speedfast` pero luego usa `speedfast_db`, y sus tablas se llaman distinto. Este proyecto usa el esquema de la Semana 8 (tablas `repartidores`, `pedidos` y `entregas`).
