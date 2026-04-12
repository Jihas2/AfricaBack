-- ─── USERS ───────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    phone       VARCHAR(50),
    password    VARCHAR(255)    NOT NULL,
    role        VARCHAR(20)     NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─── LISTINGS (annonces publiques) ───────────────────────────────────────────
CREATE TABLE listings (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255)    NOT NULL,
    description  TEXT,
    type         VARCHAR(50)     NOT NULL,
    price        DECIMAL(12, 2)  NOT NULL,
    surface      DOUBLE          NOT NULL,
    floor_number INT             NOT NULL,
    bedrooms     INT             NOT NULL,
    bathrooms    INT             NOT NULL,
    features     JSON,
    photos       JSON,
    status       VARCHAR(20)     NOT NULL DEFAULT 'BROUILLON',
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─── UNITS (unidades físicas) ─────────────────────────────────────────────────
CREATE TABLE units (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    ref          VARCHAR(50)     NOT NULL UNIQUE,
    type         VARCHAR(50)     NOT NULL,
    floor_number INT             NOT NULL,
    price        DECIMAL(12, 2)  NOT NULL,
    status       VARCHAR(20)     NOT NULL DEFAULT 'DISPONIBLE',
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─── RESERVATIONS ────────────────────────────────────────────────────────────
CREATE TABLE reservations (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id  BIGINT          NOT NULL,
    user_id     BIGINT          NOT NULL,
    message     TEXT,
    status      VARCHAR(20)     NOT NULL DEFAULT 'EN_ATTENTE',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_listing FOREIGN KEY (listing_id) REFERENCES listings (id),
    CONSTRAINT fk_res_user    FOREIGN KEY (user_id)    REFERENCES users    (id)
);

-- ─── SALES ───────────────────────────────────────────────────────────────────
CREATE TABLE sales (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id     BIGINT          NOT NULL,
    unit_id      BIGINT          NOT NULL,
    total_amount DECIMAL(12, 2)  NOT NULL,
    paid_amount  DECIMAL(12, 2)  NOT NULL DEFAULT 0,
    status       VARCHAR(20)     NOT NULL DEFAULT 'EN_COURS',
    notes        TEXT,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_buyer FOREIGN KEY (buyer_id) REFERENCES users (id),
    CONSTRAINT fk_sale_unit  FOREIGN KEY (unit_id)  REFERENCES units (id)
);

-- ─── PAYMENTS ────────────────────────────────────────────────────────────────
CREATE TABLE payments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id      BIGINT          NOT NULL,
    amount       DECIMAL(12, 2)  NOT NULL,
    method       VARCHAR(30)     NOT NULL,
    status       VARCHAR(20)     NOT NULL DEFAULT 'EN_ATTENTE',
    bank_account VARCHAR(255),
    reference    VARCHAR(100),
    payment_date DATE            NOT NULL,
    deposited_at DATE,
    notes        TEXT,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_sale FOREIGN KEY (sale_id) REFERENCES sales (id)
);
