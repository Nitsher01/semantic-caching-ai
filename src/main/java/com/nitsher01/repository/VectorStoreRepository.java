package com.nitsher01.repository;

import com.nitsher01.service.VectorUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class VectorStoreRepository {

    private final JdbcTemplate jdbc;

    public VectorStoreRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Map<String,Object>> findNearest(float[] embedding) {
        String emb = VectorUtils.toPgVectorLiteral(embedding);
        String sql = "SELECT id, question, answer, 1 - (embedding <=> ?::vector) AS similarity FROM llm_cache ORDER BY embedding <=> ?::vector LIMIT 1";
        try {
            Map<String,Object> row = jdbc.queryForMap(sql, emb, emb);
            return Optional.of(row);
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void save(String question, float[] embedding, String answer) {
        String emb = VectorUtils.toPgVectorLiteral(embedding);
        String sql = "INSERT INTO llm_cache (question, embedding, answer) VALUES (?, ?::vector, ?)";
        jdbc.update(sql, question, emb, answer);
    }
}
