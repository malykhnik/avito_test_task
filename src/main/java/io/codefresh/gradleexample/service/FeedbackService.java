package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.bid.BidResponseDto;

import java.util.UUID;

public interface FeedbackService {
    BidResponseDto submitBidFeedback(UUID bidId, String feedback, String username);
}
