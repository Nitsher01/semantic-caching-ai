package com.nitsher01.controller;


import com.nitsher01.service.SemanticCacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FaqController {

    private final SemanticCacheService service;

    public FaqController(SemanticCacheService service) {
        this.service = service;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String,String>> ask(@RequestBody Map<String,String> body) {
        String q = body.get("question");
        if (q == null || q.isBlank()) return ResponseEntity.badRequest().body(Map.of("error","question required"));
        String ans = service.getAnswer(q);
        return ResponseEntity.ok(Map.of("answer", ans));
    }
}
