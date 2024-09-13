package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.FeedbackResponseDto;
import io.codefresh.gradleexample.dto.bid.BidEditDto;
import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.entity.*;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.*;
import io.codefresh.gradleexample.mapper.BidMapper;
import io.codefresh.gradleexample.mapper.FeedbackMapper;
import io.codefresh.gradleexample.mapper.TenderMapper;
import io.codefresh.gradleexample.repository.*;
import io.codefresh.gradleexample.service.BidService;
import io.codefresh.gradleexample.utils.Helper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepo;
    private final UserRepository userRepo;
    private final TenderRepository tenderRepo;
    private final OrganizationRepository organizationRepo;
    private final FeedbackRepository feedbackRepo;
    private final Helper helper;

    @Override
    public BidResponseDto saveBid(BidRequestDto bidRequestDto) {
        Optional<User> userOptional = userRepo.findById(bidRequestDto.getAuthorId());
        Optional<Tender> tenderOptional = tenderRepo.findById(bidRequestDto.getTenderId());
        Optional<Organization> organizationOptional = organizationRepo.findById(bidRequestDto.getOrganizationId());

        if (organizationOptional.isEmpty()) throw new OrganizationNotFoundException();

        if (userOptional.isPresent()) {
            if (tenderOptional.isPresent()) {
                if (helper.isUserResponsibleForOrganization(userOptional.get().getUsername(), bidRequestDto.getOrganizationId())) {
                    return BidMapper.toDto(bidRepo.save(Bid.builder()
                            .id(UUID.randomUUID())
                            .name(bidRequestDto.getName())
                            .description(bidRequestDto.getDescription())
                            .status(Status.valueOf(String.valueOf(Status.Created)))
                            .authorType(bidRequestDto.getAuthorType())
                            .version(1L)
                            .tender(tenderOptional.get())
                            .organization(organizationOptional.get())
                            .creator(userOptional.get())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()));
                }
                throw new NotFoundUserRightsException();
            }
            throw new TenderNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<BidResponseDto> getBidsByUser(String username, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("name").ascending());

        Optional<User> userOptional = userRepo.findByUsername(username);
        List<Bid> responseDtoList = new ArrayList<>();
        if (userOptional.isPresent()) {
            Optional<List<Bid>> responseDtoListOptional = bidRepo.findBidsByCreator(userOptional.get(), pageable);
            if (responseDtoListOptional.isPresent()) responseDtoList = responseDtoListOptional.get();
        } else {
            throw new UserNotFoundException();
        }
        return BidMapper.toDtoList(responseDtoList);
    }

    @Override
    public List<BidResponseDto> getBidsByTender(UUID tenderId, String username, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("name").ascending());

        Optional<User> userOptional = userRepo.findByUsername(username);
        Optional<Tender> tenderOptional = tenderRepo.findById(tenderId);

        if (userOptional.isPresent()) {
            if (tenderOptional.isPresent()) {
                Optional<List<Bid>> bidsListOptional = bidRepo.findBidsByTenderAndCreator(tenderOptional.get(), userOptional.get(), pageable);
                if (bidsListOptional.isPresent()) {
                    return BidMapper.toDtoList(bidsListOptional.get());
                }
                throw new BidNotFoundException();
            }
            throw new TenderNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public Status getBidStatusByIdAndUser(UUID bidId, String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        User user;
        Bid bid;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            Optional<Bid> bidOptional = bidRepo.findBidByIdAndCreator(bidId, user);
            if (bidOptional.isPresent()) {
                bid = bidOptional.get();
                UUID organization_id = bid.getOrganization().getId();
                if ((bid.getStatus().equals(Status.Created) || bid.getStatus().equals(Status.Closed)) &&
                        user.getUsername().equals(username)) {
                    return bid.getStatus();
                }
                if (bid.getStatus().equals(Status.Published)
                        && helper.isUserResponsibleForOrganization(username, organization_id)) {
                    return bid.getStatus();
                }
                throw new NotFoundUserRightsException();
            }
            throw new BidNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public BidResponseDto changeStatusOfBid(UUID bidId, Status status, String username) {
        Optional<Bid> bidOptional = bidRepo.findById(bidId);
        if (bidOptional.isPresent()) {
            Bid bid = bidOptional.get();
            UUID organization_id = bid.getOrganization().getId();
            Optional<User> userOptional = userRepo.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if ((bid.getStatus().equals(Status.Created) || bid.getStatus().equals(Status.Closed)) &&
                        user.getUsername().equals(username)) {
                    bid.setStatus(status);
                    bidRepo.save(bid);
                    return BidMapper.toDto(bid);
                }
                if (bid.getStatus().equals(Status.Published)
                        && helper.isUserResponsibleForOrganization(username, organization_id)) {
                    bid.setStatus(status);
                    bidRepo.save(bid);
                    return BidMapper.toDto(bid);
                }
                throw new NotFoundUserRightsException();
            }
            throw new UserNotFoundException();
        }
        throw new BidNotFoundException();
    }

    @Override
    public Bid getBidByIdAndUsername(UUID bidId, String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        User user;
        Bid bid;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            Optional<Bid> bidOptional = bidRepo.findBidByIdAndCreator(bidId, user);
            if (bidOptional.isPresent()) {
                bid = bidOptional.get();
                UUID organization_id = bid.getOrganization().getId();
                if ((bid.getStatus().equals(Status.Created) || bid.getStatus().equals(Status.Closed)) &&
                        user.getUsername().equals(username)) {
                    return bid;
                }
                if (bid.getStatus().equals(Status.Published)
                        && helper.isUserResponsibleForOrganization(username, organization_id)) {
                    return bid;
                }
                throw new NotFoundUserRightsException();
            }
            throw new BidNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public BidResponseDto editBid(Bid bid, BidEditDto bidEditDto) {
        Optional<User> userOptional = userRepo.findByUsername(bid.getCreator().getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UUID organization_id = bid.getOrganization().getId();
            if (((bid.getStatus().equals(Status.Created) || bid.getStatus().equals(Status.Closed)) &&
                    user.getUsername().equals(bid.getCreator().getUsername())) || (bid.getStatus().equals(Status.Published)
                    && helper.isUserResponsibleForOrganization(user.getUsername(), organization_id))) {
                Bid bidUpdate = Bid.builder()
                        .id(UUID.randomUUID())
                        .name(bidEditDto.getName())
                        .description(bidEditDto.getDescription())
                        .status(bid.getStatus())
                        .authorType(bid.getAuthorType())
                        .version(bid.getVersion() + 1)
                        .tender(bid.getTender())
                        .organization(bid.getOrganization())
                        .creator(bid.getCreator())
                        .createdAt(bid.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();
                if (bidEditDto.getName() != null && !bidEditDto.getName().isEmpty())
                    bidUpdate.setName(bidEditDto.getName());
                if (bidEditDto.getDescription() != null && !bidEditDto.getDescription().isEmpty())
                    bidUpdate.setDescription(bidEditDto.getDescription());
                bidRepo.save(bidUpdate);
                return BidMapper.toDto(bidUpdate);
            }
            throw new NotFoundUserRightsException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public Bid getBidByIdAndUsernameAndVersion(UUID id, String username, Long version) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Bid> bidOptional = bidRepo.findBidByIdAndCreatorAndVersion(id, user, version);
            if (bidOptional.isPresent()) {
                return bidOptional.get();
            }
            throw new BidNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public BidResponseDto rollbackVersion(UUID bidId, Long version, String username) {
        //обработка исключений уже произошла в методе getBidByIdAndUsername
        Bid bid = getBidByIdAndUsername(bidId, username);
        Bid bidVersion = getBidByIdAndUsernameAndVersion(bidId, username, version);

        bid.setName(bidVersion.getName());
        bid.setDescription(bidVersion.getDescription());
        bid.setStatus(bidVersion.getStatus());
        bid.setAuthorType(bidVersion.getAuthorType());
        bid.setVersion(bidVersion.getVersion() + 1);
        bid.setTender(bidVersion.getTender());
        bid.setOrganization(bidVersion.getOrganization());
        bid.setCreator(bidVersion.getCreator());

        bidRepo.save(bid);

        return BidMapper.toDto(bid);
    }

    @Override
    public List<FeedbackResponseDto> getAllReviews(UUID tenderId, String authorUsername, String requesterUsername, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset, limit);

        Optional<User> userOptional = userRepo.findByUsername(requesterUsername);
        if (userOptional.isPresent()) {
            User requesterUser = userOptional.get();
            Optional<Tender> tenderOptional = tenderRepo.findTenderByIdAndCreator(tenderId, requesterUser);
            if (tenderOptional.isPresent()) {
                Tender tender = tenderOptional.get();
                Optional<User> authorOptional = userRepo.findByUsername(authorUsername);
                if (authorOptional.isPresent()) {
                    User authorUser = userOptional.get();
                    Optional<List<Bid>> bidOptional = bidRepo.findBidsByTenderAndCreator(tender, authorUser, pageable);
                    if (bidOptional.isPresent()) {
                        Optional<List<Feedback>> feedbackListOptional = feedbackRepo.findByUser(authorUser);
                        if (feedbackListOptional.isPresent()) {
                            List<Feedback> feedbackList = feedbackListOptional.get();
                            return FeedbackMapper.toDtoList(feedbackList);
                        }
                        throw new FeedbackNotFoundException();
                    }
                    throw new BidNotFoundException();
                }
                throw new UserNotFoundException();
            }
            throw new TenderNotFoundException();
        }
        throw new UserNotFoundException();
    }
}
