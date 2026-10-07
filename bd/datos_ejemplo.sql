-- Datos de ejemplo opcionales para probar la aplicación sin tener que registrar todo a mano.
-- Ejecútelos una sola vez, después de speedfast_db.sql.

USE speedfast_db;

INSERT INTO repartidores (nombre) VALUES ('Juan'), ('Camila'), ('Pedro');

INSERT INTO pedidos (direccion, tipo, estado) VALUES
    ('Santiago Centro', 'COMIDA', 'PENDIENTE'),
    ('Providencia', 'EXPRESS', 'EN_REPARTO'),
    ('Ñuñoa', 'ENCOMIENDA', 'PENDIENTE'),
    ('Recoleta', 'COMIDA', 'ENTREGADO'),
    ('Las Condes', 'EXPRESS', 'PENDIENTE');
