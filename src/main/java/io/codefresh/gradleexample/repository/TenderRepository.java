package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenderRepository extends JpaRepository<Tender, UUID> {
    @Query("SELECT t FROM Tender t WHERE (:serviceTypes IS NULL OR t.serviceType IN :serviceTypes)")
    Page<Tender> findAllByServiceType(@Param("serviceTypes") List<String> serviceTypes, Pageable pageable);
    Optional<List<Tender>> findTendersByCreator(User creator, Pageable pageable);
    Optional<Tender> findTenderByIdAndCreator(UUID id, User creator);
    Optional<Tender> findTenderByIdAndCreatorAndVersion(UUID id, User creator, Long version);
}
