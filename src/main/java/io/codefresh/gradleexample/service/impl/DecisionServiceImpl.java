package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.entity.*;
import io.codefresh.gradleexample.enumerate.BidDecision;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.BidNotFoundException;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.mapper.BidMapper;
import io.codefresh.gradleexample.repository.*;
import io.codefresh.gradleexample.service.DecisionService;
import io.codefresh.gradleexample.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionServiceImpl implements DecisionService {
    private final UserRepository userRepo;
    private final BidRepository bidRepo;
    private final DecisionRepository decisionRepo;
    private final OrganizationRepository organizationRepo;
    private final OrganizationResponsibleRepository responsibleRepo;
    private final TenderRepository tenderRepo;
    private final Helper helper;

    @Override
    public BidResponseDto submitDecision(UUID bidId, BidDecision bidDecision, String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Bid> bidOptional = bidRepo.findBidByIdAndCreator(bidId, user);
            if (bidOptional.isPresent()) {
                Bid bid = bidOptional.get();
                UUID organization_id = bid.getOrganization().getId();
                if (helper.isUserResponsibleForOrganization(username, organization_id)) {
                    decisionRepo.save(Decision.builder()
                            .id(UUID.randomUUID())
                            .decision(bidDecision)
                            .bid(bid)
                            .user(user)
                            .build());
                    return checkCountDecisionsOfBid(bid);
                }
                throw new NotFoundUserRightsException();
            }
            throw new BidNotFoundException();
        }
        throw new UserNotFoundException();
    }

    private BidResponseDto checkCountDecisionsOfBid(Bid bid) {
        List<Decision> decisionList = decisionRepo.findAllById(Collections.singleton(bid.getId()));

        UUID organization_id = bid.getOrganization().getId();
        List<OrganizationResponsible> responsible = responsibleRepo.findOrganizationResponsiblesByOrganization(organization_id);

        int quorum = Math.min(3, responsible.size());
        boolean isReject = decisionList.stream().anyMatch(decision -> decision.getDecision() == BidDecision.Rejected);

        if (!isReject) {
            long approveCount = decisionList.stream().filter(vote -> vote.getDecision() == BidDecision.Approved).count();

            if (approveCount >= quorum) {
                bid.getTender().setStatus(Status.Closed);
                tenderRepo.save(bid.getTender());
            }
        }

        return BidMapper.toDto(bid);
    }
}
