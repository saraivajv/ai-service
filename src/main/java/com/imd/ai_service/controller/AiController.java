package com.imd.ai_service.controller;

import com.imd.ai_service.dto.ReviewDTO;
import com.imd.ai_service.service.PerformanceReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/reviews")
public class AiController {

    private final PerformanceReviewService performanceReviewService;

    public AiController(PerformanceReviewService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }

    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<ReviewDTO> generatePerformanceReview(@PathVariable Long employeeId) {
        String reviewText = performanceReviewService.generateReview(employeeId);
        ReviewDTO responseDTO = new ReviewDTO();
        responseDTO.setReviewText(reviewText);
        return ResponseEntity.ok(responseDTO);
    }
}