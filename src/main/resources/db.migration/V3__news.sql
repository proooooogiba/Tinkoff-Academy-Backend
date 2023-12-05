CREATE TABLE "news" (
    id uuid PRIMARY KEY,
    source_id  varchar,
    source_name  varchar,
    author varchar,
    title varchar,
    description text,
    url varchar,
    url_to_image varchar,
    published_at  timestamp with time zone,
    content text
)