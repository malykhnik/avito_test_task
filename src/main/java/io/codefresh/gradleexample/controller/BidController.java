package io.codefresh.gradleexample.controller;

import io.codefresh.gradleexample.dto.ErrorDto;
import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.exception.TenderNotFoundException;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

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
        }
    }
}
