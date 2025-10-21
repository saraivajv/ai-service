package com.imd.ai_service.controller;

import com.imd.ai_service.dto.ReviewDTO;
import com.imd.ai_service.service.PerformanceReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai/reviews")
public class AiController {

    private final PerformanceReviewService performanceReviewService;

    public AiController(PerformanceReviewService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }

    @PostMapping("/generate/{employeeId}")
    public Mono<ResponseEntity<ReviewDTO>> generatePerformanceReview(@PathVariable Long employeeId) {
        return performanceReviewService.generateReview(employeeId)
                .map(reviewText -> {
                    ReviewDTO responseDTO = new ReviewDTO();
                    responseDTO.setReviewText(reviewText);
                    return ResponseEntity.ok(responseDTO);
                });
    }
}
