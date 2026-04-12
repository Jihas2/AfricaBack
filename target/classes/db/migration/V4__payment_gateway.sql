-- Extend reference column to hold gateway session/order IDs
ALTER TABLE payments MODIFY COLUMN reference VARCHAR(255);
