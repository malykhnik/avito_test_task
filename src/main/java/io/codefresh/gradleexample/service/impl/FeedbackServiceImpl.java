package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.entity.Feedback;
import io.codefresh.gradleexample.entity.User;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.BidNotFoundException;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.mapper.BidMapper;
import io.codefresh.gradleexample.repository.BidRepository;
import io.codefresh.gradleexample.repository.FeedbackRepository;
import io.codefresh.gradleexample.repository.UserRepository;
import io.codefresh.gradleexample.service.FeedbackService;
import io.codefresh.gradleexample.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserRepository userRepo;
    private final BidRepository bidRepo;
    private final FeedbackRepository feedbackRepo;
    private final Helper helper;

    @Override
    public BidResponseDto submitBidFeedback(UUID bidId, String bidFeedback, String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Bid> bidOptional = bidRepo.findBidByIdAndCreator(bidId, user);
            if (bidOptional.isPresent()) {
                Bid bid = bidOptional.get();
                UUID organization_id = bid.getOrganization().getId();
                if (bid.getStatus().equals(Status.Published)
                        && helper.isUserResponsibleForOrganization(username, organization_id)) {
                    feedbackRepo.save(Feedback.builder()
                            .id(UUID.randomUUID())
                            .bid(bid)
                            .user(user)
                            .feedback(bidFeedback)
                            .build());
                    return BidMapper.toDto(bid);
                }
                throw new NotFoundUserRightsException();
            }
            throw new BidNotFoundException();
        }
        throw new UserNotFoundException();
    }
}
