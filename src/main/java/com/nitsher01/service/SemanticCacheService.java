package com.nitsher01.service;

import com.nitsher01.ollama.OllamaClient;
import com.nitsher01.repository.VectorStoreRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SemanticCacheService {

    private final OllamaClient ollama;
    private final VectorStoreRepository repo;
    private static final double THRESHOLD = 0.80;

    public SemanticCacheService(OllamaClient ollama, VectorStoreRepository repo) {
        this.ollama = ollama;
        this.repo = repo;
    }

    public String getAnswer(String question) {
        float[] emb = ollama.getEmbedding(question);
        Optional<Map<String,Object>> nearest = repo.findNearest(emb);
        if (nearest.isPresent()) {
            double sim = ((Number) nearest.get().get("similarity")).doubleValue();
            if (sim >= THRESHOLD) {
                return "(cached) " + nearest.get().get("answer").toString();
            }
        }
        String answer = ollama.generateAnswer(question);
        repo.save(question, emb, answer);
        return answer;
    }
}
