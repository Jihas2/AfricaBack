-- ─── Admin note on reservations ──────────────────────────────────────────────
ALTER TABLE reservations ADD COLUMN admin_note TEXT;

-- ─── Buyer ↔ Admin messages per reservation ───────────────────────────────────
CREATE TABLE reservation_messages (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT       NOT NULL,
    sender_type    ENUM('BUYER','ADMIN') NOT NULL,
    content        TEXT         NOT NULL,
    is_read        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);
