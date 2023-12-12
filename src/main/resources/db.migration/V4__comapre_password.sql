CREATE OR REPLACE FUNCTION compare_password(username VARCHAR, plain_password VARCHAR)
    RETURNS BOOLEAN AS $$
DECLARE
    stored_password VARCHAR;
BEGIN
    -- Получаем хэшированный пароль из базы данных
    SELECT hashed_password INTO stored_password
    FROM users
    WHERE username = compare_password.username;

    -- Сравниваем хэшированный пароль с преполагаемым паролем
    RETURN stored_password = crypt(plain_password, stored_password);
END;
$$ LANGUAGE plpgsql;