package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
}
