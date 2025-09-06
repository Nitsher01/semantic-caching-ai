package com.nitsher01.ollama;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OllamaClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public OllamaClient(@Value("${ollama.api.url:http://localhost:11434/api}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @SuppressWarnings("unchecked")
    public float[] getEmbedding(String text) {
        String url = baseUrl + "/api/embeddings";
        //var req = Map.of("model", "nomic-embed-text", "prompt", text);
        var req = Map.of("model", "mxbai-embed-large", "prompt", text);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
        Map body = resp.getBody();
        if (body == null) throw new IllegalStateException("Empty response from embeddings");
        var data = (List<Double>) body.get("embedding");
        float[] v = new float[data.size()];
        for (int i = 0; i < data.size(); i++) v[i] = data.get(i).floatValue();
        return v;
    }

    @SuppressWarnings("unchecked")
    public String generateAnswer(String prompt) {
        String url = baseUrl + "/api/generate";
        var req = Map.of("model", "llama3.1:8b", "prompt", prompt, "stream", false);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);
        Map body = resp.getBody();
        if (body == null) throw new IllegalStateException("Empty response from generate");
        Object respObj = body.get("response");
        return respObj == null ? "" : respObj.toString();
    }
}
