package io.codefresh.gradleexample.controller;

import io.codefresh.gradleexample.dto.ErrorDto;
import io.codefresh.gradleexample.dto.bid.BidEditDto;
import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.enumerate.BidDecision;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.*;
import io.codefresh.gradleexample.service.BidService;
import io.codefresh.gradleexample.service.DecisionService;
import io.codefresh.gradleexample.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;
    private final DecisionService decisionService;
    private final FeedbackService feedbackService;

    @PostMapping("/new")
    public ResponseEntity<?> createBid(@Valid @RequestBody BidRequestDto bidRequestDto) {
        try {
            return ResponseEntity.ok(bidService.saveBid(bidRequestDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        } catch (OrganizationNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Organization not found"));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getBidByUsername(@RequestParam(defaultValue = "5",required = false) int limit,
                                              @RequestParam(defaultValue = "0",required = false) int offset,
                                              @RequestParam String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(400).body(null);
        if (limit <= 0 || offset < 0) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("limit or offset less then 0"));
        }
        try {
            return ResponseEntity.ok(bidService.getBidsByUser(username, limit, offset));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        }
    }

    @GetMapping("/{tenderId}/list")
    public ResponseEntity<?> getBidsByTender(@PathVariable UUID tenderId,
                                             @RequestParam String username,
                                             @RequestParam(defaultValue = "5",required = false) int limit,
                                             @RequestParam(defaultValue = "0",required = false) int offset) {
        if (username == null || username.isEmpty() || tenderId == null) return ResponseEntity.status(400).body(null);
        if (limit <= 0 || offset < 0) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("limit or offset less then 0"));
        }
        try {
            return ResponseEntity.ok(bidService.getBidsByTender(tenderId, username, limit, offset));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bids not found"));
        }
    }

    @GetMapping("/{bidId}/status")
    public ResponseEntity<?> getBidStatusById(@PathVariable UUID bidId, String username) {
        if (username == null || username.isEmpty() || bidId == null) return ResponseEntity.status(400).body(null);
        try {
            return ResponseEntity.ok(bidService.getBidStatusByIdAndUser(bidId, username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        }
    }

    @PutMapping("/{bidId}/status")
    public ResponseEntity<?> updateBidStatusById(@PathVariable UUID bidId,
                                                 @RequestParam Status status,
                                                 @RequestParam String username) {
        if (bidId == null || status == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parameter is empty"));
        }
        try {
            return ResponseEntity.ok(bidService.changeStatusOfBid(bidId, status, username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        }
    }

    @PatchMapping("/{bidId}/edit")
    public ResponseEntity<?> editBid(@PathVariable UUID bidId,
                                     @RequestParam String username,
                                     @RequestBody BidEditDto bidEditDto) {
        if (bidId == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            Bid bid = bidService.getBidByIdAndUsername(bidId, username);
            return ResponseEntity.ok(bidService.editBid(bid, bidEditDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        }
    }

    @PutMapping("/{bidId}/submit_decision")
    public ResponseEntity<?> submitDecision(@PathVariable UUID bidId,
                                            @RequestParam BidDecision bidDecision,
                                            @RequestParam String username) {
        if (bidId == null || username == null || username.isEmpty() || bidDecision == null) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            return ResponseEntity.ok(decisionService.submitDecision(bidId, bidDecision, username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        } catch (OrganizationNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Organization not found"));
        }
    }

    @PutMapping("/{bidId}/feedback")
    public ResponseEntity<?> bidFeedback(@PathVariable UUID bidId,
                                         @RequestParam String bidFeedback,
                                         @RequestParam String username) {
        if (bidId == null || username == null || username.isEmpty() || bidFeedback == null || bidFeedback.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            return ResponseEntity.ok(feedbackService.submitBidFeedback(bidId, bidFeedback, username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        }
    }

    @PutMapping("/{bidId}/rollback/{version}")
    public ResponseEntity<?> rollbackVersion(@PathVariable UUID bidId,
                                             @PathVariable Long version,
                                             @RequestParam String username) {
        if (bidId == null || username == null || username.isEmpty() || version == null) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            return ResponseEntity.ok(bidService.rollbackVersion(bidId, version, username));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid or version not found"));
        }
    }

    @GetMapping("/{tenderId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable UUID tenderId,
                                        @RequestParam String authorUsername,
                                        @RequestParam String requesterUsername,
                                        @RequestParam(defaultValue = "5",required = false) int limit,
                                        @RequestParam(defaultValue = "0",required = false) int offset) {
        if (limit <= 0 || offset < 0) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("limit or offset less then 0"));
        }
        if (tenderId == null || authorUsername == null || authorUsername.isEmpty()
                || requesterUsername == null || requesterUsername.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            return ResponseEntity.ok(bidService.getAllReviews(tenderId, authorUsername, requesterUsername, limit, offset));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException | FeedbackNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender or feedbacks not found"));
        } catch (BidNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Bid not found"));
        }
    }
}
