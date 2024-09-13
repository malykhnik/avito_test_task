package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    Optional<List<Bid>> findBidsByCreator(User creator);
    Optional<List<Bid>> findBidsByTenderAndCreator(Tender tender, User creator);
    Optional<Bid> findBidByIdAndCreator(UUID id, User creator);
    Optional<Bid> findBidByIdAndCreatorAndVersion(UUID id, User creator, Long version);
}
