# 🚀 Semantic Cache with Ollama + Postgres + pgvector

This project demonstrates how to build a **semantic caching layer** on top of an LLM-powered FAQ system using:

- **[Ollama](https://ollama.com/)** for running local models
- **Embeddings models** like `nomic-embed-text` and `mxbai-embed-large`
- **Postgres + pgvector** for similarity search
- **Spring Boot** for REST APIs
- **JUnit + Spring Boot ITs** for integration testing

The goal: cache answers **by meaning**, not exact text, so that semantically similar questions reuse previously computed answers instead of calling the LLM again.

---

## 🛠️ Architecture Overview

1. **User query** → comes in via REST API.
2. **Embedding lookup** → query is converted into a dense vector using Ollama’s embedding models.
3. **Cache search** → query vector is compared against cached vectors in Postgres (`pgvector` index).
4. - If match found → return cached answer (fast ✅).
- If no match → forward query to LLM, compute embedding, store `(question, embedding, answer)` in Postgres for reuse.

---

## 📦 Requirements

- **Java 17+**
- **Spring Boot 3.x**
- **Postgres 15+ with pgvector**
- **Ollama** running locally

Install Ollama:
```bash
brew install ollama          # macOS
# or follow https://ollama.com/download for Linux/Windows
```

Pull required models:
```bash
ollama pull nomic-embed-text
ollama pull mxbai-embed-large
ollama pull llama3.1:8b
```

---

## 🗄️ Database Setup

Enable `pgvector` in Postgres:
```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

Create the cache table (default for 768-dim embeddings):
```sql
CREATE TABLE llm_cache (
    id SERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    embedding VECTOR(768),
    answer TEXT NOT NULL
);
```

> ⚠️ If using `mxbai-embed-large` (1024 dimensions), adjust table schema accordingly:
```sql
CREATE TABLE llm_cache_mxbai (
    id SERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    embedding VECTOR(1024),
    answer TEXT NOT NULL
);
```

---

## ▶️ Running Locally

Start Postgres and Ollama.  
Then run the Spring Boot app:

```bash
./mvnw spring-boot:run
```

API examples:

- **Ask a question (cached or via LLM)**
  ```bash
  curl -X POST "http://localhost:8080/ask"        -H "Content-Type: application/json"        -d '{"question": "What is Spring Boot?"}'
  ```

- **Check cached entries**
  ```bash
  curl http://localhost:8080/cache
  ```

---

## 🧪 Tests

### Unit Tests
- Verify embedding parsing, cache logic, similarity scoring.

### Integration Tests
- **`SemanticCacheIT`** → end-to-end validation of caching against Postgres.
- **`ModelComparisonIT`** → compares `nomic-embed-text` vs `mxbai-embed-large` on a toy FAQ set for **accuracy & latency**.

Run tests:
```bash
./mvnw test
```

Sample output from `ModelComparisonIT`:
```
=== Embedding Model Comparison ===
Model: nomic-embed-text    | accuracy: 0.75 | avg latency: 42.3 ms | dims: 768  | notes: OK
Model: mxbai-embed-large   | accuracy: 1.00 | avg latency: 55.8 ms | dims: 1024 | notes: OK
```

---

## 📊 Why Compare Models?

- **`nomic-embed-text` (768-dim)** → lightweight, fast, good for smaller workloads.
- **`mxbai-embed-large` (1024-dim)** → higher dimensional vectors, generally more accurate at capturing semantics.
- Tradeoff: latency vs accuracy.

---

## 🔮 Next Steps

- Support **recall@k** (e.g., top-3 semantic matches).
- Benchmark caching hit-rate on larger datasets.
- Add multi-model table support (`llm_cache_768`, `llm_cache_1024`) with model routing.
- Deploy with Docker Compose (Postgres + Ollama + App).

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch
3. Run tests locally with Ollama + Postgres
4. Submit PR  
