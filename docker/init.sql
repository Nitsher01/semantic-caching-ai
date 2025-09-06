CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS llm_cache (
 id SERIAL PRIMARY KEY,
 question TEXT,
 embedding vector(768),
 answer TEXT
);