USE wefood;

CREATE TABLE IF NOT EXISTS perfis_acesso (
    id_perfil BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    descricao VARCHAR(120) NOT NULL
) ENGINE=InnoDB;

INSERT IGNORE INTO perfis_acesso (codigo, descricao) VALUES
('CLIENTE', 'Pode descobrir restaurantes e realizar pedidos'),
('RESTAURANTE', 'Pode administrar somente os próprios estabelecimentos'),
('ENTREGADOR', 'Pode visualizar e atualizar entregas atribuídas'),
('ADMIN', 'Pode moderar e administrar a plataforma');

CREATE TABLE IF NOT EXISTS usuario_perfil (
    id_usuario BIGINT UNSIGNED NOT NULL,
    id_perfil BIGINT UNSIGNED NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_perfil),
    CONSTRAINT fk_up_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_up_perfil FOREIGN KEY (id_perfil) REFERENCES perfis_acesso(id_perfil) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS restaurante_responsaveis (
    id_restaurante BIGINT UNSIGNED NOT NULL,
    id_usuario BIGINT UNSIGNED NOT NULL,
    papel ENUM('PROPRIETARIO','GERENTE','OPERADOR') NOT NULL DEFAULT 'PROPRIETARIO',
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_restaurante, id_usuario),
    CONSTRAINT fk_rr_restaurante FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id_restaurante) ON DELETE CASCADE,
    CONSTRAINT fk_rr_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_rr_usuario (id_usuario)
) ENGINE=InnoDB;

ALTER TABLE restaurantes
    ADD COLUMN status_aprovacao ENUM('PENDENTE','APROVADO','REJEITADO','SUSPENSO') NOT NULL DEFAULT 'PENDENTE',
    ADD COLUMN motivo_rejeicao VARCHAR(500) NULL,
    ADD COLUMN pedido_minimo_entrega DECIMAL(10,2) NOT NULL DEFAULT 0.00;

CREATE TABLE IF NOT EXISTS horarios_restaurante (
    id_horario BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    dia_semana TINYINT UNSIGNED NOT NULL COMMENT '1=segunda ... 7=domingo',
    hora_abertura TIME NOT NULL,
    hora_fechamento TIME NOT NULL,
    fechado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_horario_dia CHECK (dia_semana BETWEEN 1 AND 7),
    CONSTRAINT fk_hr_restaurante FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id_restaurante) ON DELETE CASCADE,
    INDEX idx_horario_restaurante_dia (id_restaurante, dia_semana)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS solicitacoes_cancelamento_pedido (
    id_solicitacao BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL,
    id_usuario_solicitante BIGINT UNSIGNED NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    status ENUM('PENDENTE','ACEITA','RECUSADA') NOT NULL DEFAULT 'PENDENTE',
    resposta VARCHAR(500),
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_scp_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_scp_usuario FOREIGN KEY (id_usuario_solicitante) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_scp_pedido_status (id_pedido, status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cupons_usuario (
    id_cupom BIGINT UNSIGNED NOT NULL,
    id_usuario BIGINT UNSIGNED NOT NULL,
    utilizado_em DATETIME NULL,
    id_pedido BIGINT UNSIGNED NULL,
    PRIMARY KEY (id_cupom, id_usuario),
    CONSTRAINT fk_cu_cupom FOREIGN KEY (id_cupom) REFERENCES cupons(id_cupom) ON DELETE CASCADE,
    CONSTRAINT fk_cu_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_cu_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS logs_administrativos (
    id_log BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario_admin BIGINT UNSIGNED NULL,
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(80) NOT NULL,
    id_entidade BIGINT UNSIGNED NULL,
    detalhes JSON NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_admin FOREIGN KEY (id_usuario_admin) REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    INDEX idx_log_entidade (entidade, id_entidade),
    INDEX idx_log_data (criado_em)
) ENGINE=InnoDB;
