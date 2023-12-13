insert into users (name, password) VALUES
    ('Admin', crypt('test', gen_salt('md5')) );
