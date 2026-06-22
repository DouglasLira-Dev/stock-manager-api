-- Adiciona campos de auditoria

-- Para a tabela users
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by VARCHAR(150);
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by VARCHAR(150);

-- Para a tabela products
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_by VARCHAR(150);
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_by VARCHAR(150);

-- Gatilho para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();