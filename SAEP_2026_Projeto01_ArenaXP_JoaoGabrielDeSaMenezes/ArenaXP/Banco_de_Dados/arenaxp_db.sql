-
DROP DATABASE IF EXISTS arenaxp_db;

CREATE DATABASE IF NOT EXISTS arenaxp_db
    CHARACTER SET utf8mb4;

USE arenaxp_db;

CREATE TABLE usuarios (
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    nome    VARCHAR(100) NOT NULL,
    email   VARCHAR(120) NOT NULL,
    senha   VARCHAR(255) NOT NULL,   -- hash BCrypt
    perfil  VARCHAR(30)  NOT NULL,   -- ADMIN, TECNICO
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
) ENGINE = InnoDB;


CREATE TABLE equipamentos (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    codigo_patrimonio     VARCHAR(30)  NOT NULL,
    nome                  VARCHAR(120) NOT NULL,
    categoria             VARCHAR(50)  NOT NULL,
    especificacao_tecnica VARCHAR(255) NOT NULL,
    quantidade_atual      INT          NOT NULL,
    estoque_minimo        INT          NOT NULL,
    CONSTRAINT pk_equipamentos PRIMARY KEY (id),
    CONSTRAINT uk_equipamentos_codigo UNIQUE (codigo_patrimonio),
    CONSTRAINT ck_equipamentos_qtd_atual CHECK (quantidade_atual >= 0),
    CONSTRAINT ck_equipamentos_qtd_min CHECK (estoque_minimo >= 0)
) ENGINE = InnoDB;



CREATE TABLE movimentacoes (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    tipo_movimentacao  VARCHAR(15)  NOT NULL,
    quantidade         INT          NOT NULL,
    data_hora          DATETIME     NOT NULL,
    observacao_evento  VARCHAR(255) NULL,
    equipamento_id     BIGINT       NOT NULL,
    usuario_id         BIGINT       NOT NULL,
    CONSTRAINT pk_movimentacoes PRIMARY KEY (id),
    CONSTRAINT ck_movimentacoes_tipo CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA')),
    CONSTRAINT ck_movimentacoes_qtd CHECK (quantidade > 0),
    CONSTRAINT fk_movimentacoes_equipamento FOREIGN KEY (equipamento_id) REFERENCES equipamentos (id),
    CONSTRAINT fk_movimentacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE = InnoDB;


--  Senhas em BCrypt:  adm@arenaxp.com = adm123 | ambos os técnicos = tecnico123
INSERT INTO usuarios (nome, email, senha, perfil) VALUES
('Administrador ArenaXP', 'adm@arenaxp.com',  '$2a$10$URmI0Ev9023FKVWt0fsCeu5ue38qk34kzrSFDKtFjOC8YVyR8eFfK', 'ADMIN'),
('João Técnico',        'tecnicoum@arenaxp.com', '$2a$10$Bjso6wiOOvVKSYrmGMbr3.SMYkLSio.K.hUBCv/Bn1eyTsdsxUfA2', 'TECNICO'),
('Maria Técnica',        'tecnicodois@arenaxp.com', '$2a$10$Bjso6wiOOvVKSYrmGMbr3.SMYkLSio.K.hUBCv/Bn1eyTsdsxUfA2', 'TECNICO');


--  Headset (4 / mín. 3) está próximo do limite; Placa de Captura (2 / mín. 2) já  em alerta.
INSERT INTO equipamentos (codigo_patrimonio, nome, categoria, especificacao_tecnica, quantidade_atual, estoque_minimo) VALUES
('AXP-MSE-001', 'Mouse Gamer Ultraleve Pro',  'Mouse',   '8000Hz Wireless, 58g',                    12, 5),
('AXP-TEC-001', 'Teclado Mecânico RGB',       'Teclado', 'Switch Red, ABNT2 TKL',                    8, 4),
('AXP-HDS-001', 'Headset Surround 7.1',       'Headset', 'Cancelamento de ruído, USB, 7.1 virtual',  4, 3),
('AXP-MON-001', 'Monitor 240Hz 24 polegadas', 'Monitor', 'Full HD IPS, 240Hz, 1ms',                  6, 2),
('AXP-CAP-001', 'Placa de Captura 4K',        'Captura', 'HDMI 2.1, 4K60, USB-C',                    2, 2);


INSERT INTO movimentacoes (tipo_movimentacao, quantidade, data_hora, observacao_evento, equipamento_id, usuario_id) VALUES
('ENTRADA', 10, '2026-11-10 09:00:00', 'Reposição de estoque - compra mensal',            1, 1),
('SAIDA',    2, '2026-11-14 18:30:00', 'Copa Universitária FPS - Palco A',                3, 2),
('SAIDA',    3, '2026-11-15 14:15:00', 'Torneio Regional MOBA - Mesas 1 a 3',             1, 3),
('SAIDA',    1, '2026-11-20 20:00:00', 'Transmissão da Final de Luta - Estúdio B',        5, 2);