CREATE INDEX news_content_idx ON news USING gin(to_tsvector('english', content));
