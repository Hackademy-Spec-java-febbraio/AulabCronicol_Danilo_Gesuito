create TABLE images (
    id BIGINT auto_increment PRIMARY KEY,
    patch VARCHAR(255) NOT NULL,
    article_id BIGINT,
    FOREIGN KEY (article_id) REFERENCES articles(id)
);