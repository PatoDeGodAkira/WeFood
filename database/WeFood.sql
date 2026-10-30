DROP DATABASE IF EXISTS wefood;
CREATE DATABASE wefood;

USE wefood;

CREATE TABLE usuarios (
    id_usuario BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(25),
    foto_url VARCHAR(500),
    data_nascimento DATE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE configuracoes_usuario (
    id_usuario BIGINT UNSIGNED PRIMARY KEY,
    receber_notificacoes BOOLEAN NOT NULL DEFAULT TRUE,
    receber_ofertas BOOLEAN NOT NULL DEFAULT TRUE,
    compartilhar_localizacao BOOLEAN NOT NULL DEFAULT TRUE,
    perfil_publico BOOLEAN NOT NULL DEFAULT TRUE,
    idioma VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_config_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE enderecos (
    id_endereco BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    apelido VARCHAR(50) NOT NULL DEFAULT 'Principal',
    cep VARCHAR(10),
    logradouro VARCHAR(180) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    referencia VARCHAR(255),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_endereco_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    INDEX idx_endereco_usuario (id_usuario),
    INDEX idx_endereco_localizacao (latitude, longitude)
) ENGINE=InnoDB;

CREATE TABLE restaurantes (
    id_restaurante BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    telefone VARCHAR(25),
    email VARCHAR(180),
    cnpj VARCHAR(18) UNIQUE,
    logo_url VARCHAR(500),
    capa_url VARCHAR(500),

    logradouro VARCHAR(180),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado CHAR(2) NOT NULL,
    cep VARCHAR(10),

    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),

    taxa_entrega DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    pedido_minimo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tempo_entrega_min INT UNSIGNED DEFAULT 30,
    aberto BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_restaurante_localizacao (latitude, longitude),
    INDEX idx_restaurante_cidade (cidade, estado),
    INDEX idx_restaurante_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE categorias_restaurante (
    id_categoria BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    imagem_url VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE restaurante_categoria (
    id_restaurante BIGINT UNSIGNED NOT NULL,
    id_categoria BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (id_restaurante, id_categoria),

    CONSTRAINT fk_rc_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE,

    CONSTRAINT fk_rc_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias_restaurante(id_categoria)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE categorias_produto (
    id_categoria_produto BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    nome VARCHAR(100) NOT NULL,
    ordem INT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_categoria_produto_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE,

    UNIQUE KEY uk_categoria_restaurante_nome (id_restaurante, nome)
) ENGINE=InnoDB;

CREATE TABLE produtos (
    id_produto BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    id_categoria_produto BIGINT UNSIGNED,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    imagem_url VARCHAR(500),
    preco DECIMAL(10,2) NOT NULL,
    preco_promocional DECIMAL(10,2),
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    destaque BOOLEAN NOT NULL DEFAULT FALSE,
    tempo_preparo_min INT UNSIGNED DEFAULT 15,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_produto_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria_produto)
        REFERENCES categorias_produto(id_categoria_produto)
        ON DELETE SET NULL,

    INDEX idx_produto_restaurante (id_restaurante),
    INDEX idx_produto_categoria (id_categoria_produto),
    INDEX idx_produto_nome (nome)
) ENGINE=InnoDB;

CREATE TABLE adicionais (
    id_adicional BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(255),
    preco DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_adicional_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE produto_adicional (
    id_produto BIGINT UNSIGNED NOT NULL,
    id_adicional BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (id_produto, id_adicional),

    CONSTRAINT fk_pa_produto
        FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
        ON DELETE CASCADE,

    CONSTRAINT fk_pa_adicional
        FOREIGN KEY (id_adicional) REFERENCES adicionais(id_adicional)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE favoritos (
    id_usuario BIGINT UNSIGNED NOT NULL,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_usuario, id_restaurante),

    CONSTRAINT fk_favorito_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorito_restaurante
        FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE avaliacoes_restaurante (
    id_avaliacao BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    id_pedido BIGINT UNSIGNED NULL,
    pontuacao DECIMAL(2,1) NOT NULL,
    comentario TEXT,
    foto_url VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_pontuacao_restaurante
        CHECK (pontuacao >= 0 AND pontuacao <= 5),

    CONSTRAINT fk_avaliacao_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_avaliacao_restaurante
        FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE,

    INDEX idx_avaliacao_restaurante (id_restaurante),
    INDEX idx_avaliacao_usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE cupons (
    id_cupom BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_restaurante BIGINT UNSIGNED NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    tipo_desconto ENUM('PERCENTUAL','VALOR_FIXO','FRETE_GRATIS') NOT NULL,
    valor_desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_minimo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    desconto_maximo DECIMAL(10,2),
    limite_uso INT UNSIGNED,
    usos_realizados INT UNSIGNED NOT NULL DEFAULT 0,
    inicio DATETIME NOT NULL,
    fim DATETIME NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_cupom_restaurante
        FOREIGN KEY (id_restaurante)
        REFERENCES restaurantes(id_restaurante)
        ON DELETE CASCADE,

    INDEX idx_cupom_codigo (codigo),
    INDEX idx_cupom_validade (inicio, fim)
) ENGINE=InnoDB;

CREATE TABLE pedidos (
    id_pedido BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    id_restaurante BIGINT UNSIGNED NOT NULL,
    id_endereco BIGINT UNSIGNED NULL,
    id_cupom BIGINT UNSIGNED NULL,

    status ENUM(
        'AGUARDANDO_PAGAMENTO',
        'PAGAMENTO_APROVADO',
        'RECEBIDO',
        'PREPARANDO',
        'PRONTO',
        'SAIU_PARA_ENTREGA',
        'ENTREGUE',
        'CANCELADO'
    ) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',

    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    taxa_entrega DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    observacao TEXT,

    endereco_logradouro VARCHAR(180),
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(100),
    endereco_bairro VARCHAR(100),
    endereco_cidade VARCHAR(100),
    endereco_estado CHAR(2),
    endereco_cep VARCHAR(10),
    endereco_referencia VARCHAR(255),
    endereco_latitude DECIMAL(10,7),
    endereco_longitude DECIMAL(10,7),

    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmado_em DATETIME NULL,
    entregue_em DATETIME NULL,
    cancelado_em DATETIME NULL,

    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),

    CONSTRAINT fk_pedido_restaurante
        FOREIGN KEY (id_restaurante) REFERENCES restaurantes(id_restaurante),

    CONSTRAINT fk_pedido_endereco
        FOREIGN KEY (id_endereco) REFERENCES enderecos(id_endereco)
        ON DELETE SET NULL,

    CONSTRAINT fk_pedido_cupom
        FOREIGN KEY (id_cupom) REFERENCES cupons(id_cupom)
        ON DELETE SET NULL,

    INDEX idx_pedido_usuario (id_usuario),
    INDEX idx_pedido_restaurante (id_restaurante),
    INDEX idx_pedido_status (status),
    INDEX idx_pedido_data (criado_em)
) ENGINE=InnoDB;

CREATE TABLE itens_pedido (
    id_item_pedido BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL,
    id_produto BIGINT UNSIGNED NOT NULL,

    nome_produto VARCHAR(150) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    quantidade INT UNSIGNED NOT NULL DEFAULT 1,
    observacao VARCHAR(500),
    subtotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto) REFERENCES produtos(id_produto),

    INDEX idx_item_pedido (id_pedido)
) ENGINE=InnoDB;

CREATE TABLE itens_pedido_adicional (
    id_item_pedido BIGINT UNSIGNED NOT NULL,
    id_adicional BIGINT UNSIGNED NOT NULL,
    nome_adicional VARCHAR(120) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantidade INT UNSIGNED NOT NULL DEFAULT 1,

    PRIMARY KEY (id_item_pedido, id_adicional),

    CONSTRAINT fk_ipa_item
        FOREIGN KEY (id_item_pedido)
        REFERENCES itens_pedido(id_item_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_ipa_adicional
        FOREIGN KEY (id_adicional)
        REFERENCES adicionais(id_adicional)
) ENGINE=InnoDB;


CREATE TABLE historico_pedido (
    id_historico BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL,
    status ENUM(
        'AGUARDANDO_PAGAMENTO',
        'PAGAMENTO_APROVADO',
        'RECEBIDO',
        'PREPARANDO',
        'PRONTO',
        'SAIU_PARA_ENTREGA',
        'ENTREGUE',
        'CANCELADO'
    ) NOT NULL,
    mensagem VARCHAR(255),
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historico_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    INDEX idx_historico_pedido_data (id_pedido, criado_em)
) ENGINE=InnoDB;

CREATE TABLE formas_pagamento (
    id_forma_pagamento BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    tipo ENUM(
        'PIX',
        'CARTAO_CREDITO',
        'CARTAO_DEBITO',
        'DINHEIRO'
    ) NOT NULL,

    titular VARCHAR(150),
    identificador VARCHAR(255),
    bandeira VARCHAR(50),
    ultimos_digitos CHAR(4),
    validade_mes TINYINT UNSIGNED,
    validade_ano SMALLINT UNSIGNED,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pagamento_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pagamentos (
    id_pagamento BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL,
    id_forma_pagamento BIGINT UNSIGNED NULL,

    metodo ENUM(
        'PIX',
        'CARTAO_CREDITO',
        'CARTAO_DEBITO',
        'DINHEIRO',
        'CARTEIRA'
    ) NOT NULL,

    status ENUM(
        'PENDENTE',
        'PROCESSANDO',
        'APROVADO',
        'RECUSADO',
        'ESTORNADO',
        'CANCELADO'
    ) NOT NULL DEFAULT 'PENDENTE',

    valor DECIMAL(10,2) NOT NULL,
    transacao_externa VARCHAR(150),
    pago_em DATETIME NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_pagamento_forma
        FOREIGN KEY (id_forma_pagamento)
        REFERENCES formas_pagamento(id_forma_pagamento)
        ON DELETE SET NULL,

    UNIQUE KEY uk_pagamento_transacao (transacao_externa)
) ENGINE=InnoDB;

CREATE TABLE carteiras (
    id_carteira BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,
    saldo DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_carteira_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE transacoes_carteira (
    id_transacao BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_carteira BIGINT UNSIGNED NOT NULL,
    tipo ENUM('CREDITO','DEBITO','ESTORNO') NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    descricao VARCHAR(255),
    id_pedido BIGINT UNSIGNED NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transacao_carteira
        FOREIGN KEY (id_carteira) REFERENCES carteiras(id_carteira)
        ON DELETE CASCADE,

    CONSTRAINT fk_transacao_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE pontos_usuario (
    id_usuario BIGINT UNSIGNED PRIMARY KEY,
    saldo_pontos INT NOT NULL DEFAULT 0,
    atualizado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_pontos_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE movimentacoes_pontos (
    id_movimentacao BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    tipo ENUM('CREDITO','DEBITO','EXPIRADO','AJUSTE') NOT NULL,
    pontos INT NOT NULL,
    descricao VARCHAR(255),
    id_pedido BIGINT UNSIGNED NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mov_pontos_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_mov_pontos_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE entregadores (
    id_entregador BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(25),
    foto_url VARCHAR(500),
    veiculo VARCHAR(100),
    placa VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    disponivel BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE entregas (
    id_entrega BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL UNIQUE,
    id_entregador BIGINT UNSIGNED NULL,

    status ENUM(
        'AGUARDANDO_ENTREGADOR',
        'ENTREGADOR_A_CAMINHO',
        'RETIRADO_NO_RESTAURANTE',
        'A_CAMINHO_DO_CLIENTE',
        'CHEGANDO',
        'ENTREGUE',
        'CANCELADA'
    ) NOT NULL DEFAULT 'AGUARDANDO_ENTREGADOR',

    previsao_minutos INT UNSIGNED,
    inicio_entrega DATETIME NULL,
    retirada_em DATETIME NULL,
    entrega_em DATETIME NULL,

    CONSTRAINT fk_entrega_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_entrega_entregador
        FOREIGN KEY (id_entregador) REFERENCES entregadores(id_entregador)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE rastreamento_entrega (
    id_rastreamento BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_entrega BIGINT UNSIGNED NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    status VARCHAR(100),
    registrado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rastreamento_entrega
        FOREIGN KEY (id_entrega) REFERENCES entregas(id_entrega)
        ON DELETE CASCADE,

    INDEX idx_rastreamento_entrega_data (id_entrega, registrado_em)
) ENGINE=InnoDB;

CREATE TABLE avaliacoes_entrega (
    id_avaliacao_entrega BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT UNSIGNED NOT NULL UNIQUE,
    id_usuario BIGINT UNSIGNED NOT NULL,
    id_entregador BIGINT UNSIGNED NULL,
    pontuacao DECIMAL(2,1) NOT NULL,
    comentario TEXT,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_pontuacao_entrega
        CHECK (pontuacao >= 0 AND pontuacao <= 5),

    CONSTRAINT fk_av_entrega_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_av_entrega_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_av_entrega_entregador
        FOREIGN KEY (id_entregador) REFERENCES entregadores(id_entregador)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE notificacoes (
    id_notificacao BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo VARCHAR(50),
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    id_pedido BIGINT UNSIGNED NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notificacao_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE,

    CONSTRAINT fk_notificacao_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE SET NULL,

    INDEX idx_notificacao_usuario (id_usuario, lida, criado_em)
) ENGINE=InnoDB;


DELIMITER $$

-- Cria configurações básicas automaticamente ao cadastrar usuário
CREATE TRIGGER trg_usuario_after_insert
AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO configuracoes_usuario (id_usuario)
    VALUES (NEW.id_usuario);

    INSERT INTO carteiras (id_usuario)
    VALUES (NEW.id_usuario);

    INSERT INTO pontos_usuario (id_usuario)
    VALUES (NEW.id_usuario);
END$$

-- Registra automaticamente a criação do pedido no histórico
CREATE TRIGGER trg_pedido_after_insert
AFTER INSERT ON pedidos
FOR EACH ROW
BEGIN
    INSERT INTO historico_pedido (id_pedido, status, mensagem)
    VALUES (
        NEW.id_pedido,
        NEW.status,
        'Pedido criado'
    );
END$$

-- Registra alterações de status
CREATE TRIGGER trg_pedido_after_update
AFTER UPDATE ON pedidos
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO historico_pedido (id_pedido, status, mensagem)
        VALUES (
            NEW.id_pedido,
            NEW.status,
            CONCAT('Status alterado para ', NEW.status)
        );
    END IF;
END$$

DELIMITER ;

CREATE OR REPLACE VIEW vw_restaurantes_pontuacao AS
SELECT
    r.id_restaurante,
    r.nome,
    r.descricao,
    r.logo_url,
    r.capa_url,
    r.taxa_entrega,
    r.tempo_entrega_min,
    r.aberto,
    r.latitude,
    r.longitude,
    COALESCE(ROUND(AVG(a.pontuacao), 1), 0) AS pontuacao,
    COUNT(a.id_avaliacao) AS quantidade_avaliacoes
FROM restaurantes r
LEFT JOIN avaliacoes_restaurante a
    ON a.id_restaurante = r.id_restaurante
   AND a.ativo = TRUE
GROUP BY
    r.id_restaurante,
    r.nome,
    r.descricao,
    r.logo_url,
    r.capa_url,
    r.taxa_entrega,
    r.tempo_entrega_min,
    r.aberto,
    r.latitude,
    r.longitude;

CREATE OR REPLACE VIEW vw_cardapio_restaurante AS
SELECT
    p.id_produto,
    p.id_restaurante,
    cp.nome AS categoria,
    p.nome,
    p.descricao,
    p.imagem_url,
    p.preco,
    p.preco_promocional,
    CASE
        WHEN p.preco_promocional IS NOT NULL
             AND p.preco_promocional < p.preco
        THEN p.preco_promocional
        ELSE p.preco
    END AS preco_atual,
    p.disponivel,
    p.destaque
FROM produtos p
LEFT JOIN categorias_produto cp
    ON cp.id_categoria_produto = p.id_categoria_produto;

CREATE OR REPLACE VIEW vw_pedidos_usuario AS
SELECT
    p.id_pedido,
    p.id_usuario,
    r.nome AS restaurante,
    r.logo_url,
    p.status,
    p.subtotal,
    p.taxa_entrega,
    p.desconto,
    p.total,
    p.criado_em,
    p.entregue_em
FROM pedidos p
INNER JOIN restaurantes r
    ON r.id_restaurante = p.id_restaurante;


INSERT INTO categorias_restaurante (nome, descricao) VALUES
('Hambúrguer', 'Hambúrgueres e combos'),
('Pizza', 'Pizzas e sabores variados'),
('Brasileira', 'Comida brasileira'),
('Japonesa', 'Comida japonesa'),
('Mexicana', 'Comida mexicana'),
('Doces', 'Sobremesas e doces'),
('Bebidas', 'Bebidas em geral');
