package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.bid.BidEditDto;
import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.enumerate.Status;

import java.util.List;
import java.util.UUID;

public interface BidService {
    BidResponseDto saveBid(BidRequestDto bidRequestDto);
    List<BidResponseDto> getBidsByUser(String username);
    List<BidResponseDto> getBidsByTender(UUID tenderId, String username);
    Status getBidStatusByIdAndUser(UUID bidId, String username);

    BidResponseDto changeStatusOfBid(UUID bidId, Status status, String username);
    Bid getBidByIdAndUsername(UUID bidId, String username);
    BidResponseDto editBid(Bid bid, BidEditDto bidEditDto);
}
