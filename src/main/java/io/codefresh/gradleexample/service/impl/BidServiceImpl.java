package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.entity.User;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.repository.BidRepository;
import io.codefresh.gradleexample.repository.UserRepository;
import io.codefresh.gradleexample.service.BidService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepo;
    private final UserRepository userRepo;
    @Override
    public BidResponseDto saveBid(BidRequestDto bidRequestDto) {
        Optional<User> userOptional = userRepo.findById(bidRequestDto.getAuthorId());
        if (userOptional.isPresent()) {

        }
        throw new UserNotFoundException();
    }
}
