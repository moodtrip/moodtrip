package com.example.demo.controller;

import com.example.demo.dto.EmotionRequest;
import com.example.demo.model.PlaceInfo;
import com.example.demo.service.RecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public ResponseEntity<List<PlaceInfo>> recommend(@RequestBody EmotionRequest request) throws Exception {
        List<PlaceInfo> places = recommendService.recommendPlaces(request.getInput());
        return ResponseEntity.ok(places);
    }
}
