package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenderRepository extends JpaRepository<Tender, UUID> {
    Optional<List<Tender>> findTendersByCreator(User user);
    Optional<Tender> findTenderByIdAndCreator(UUID id, User user);
}
