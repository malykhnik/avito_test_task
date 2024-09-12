package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.enumerate.BidDecision;

import java.util.UUID;

public interface DecisionService {
    BidResponseDto submitDecision(UUID bidId, BidDecision bidDecision, String username);
}
