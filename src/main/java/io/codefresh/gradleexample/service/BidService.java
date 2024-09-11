package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;

public interface BidService {
    BidResponseDto saveBid(BidRequestDto bidRequestDto);
}
