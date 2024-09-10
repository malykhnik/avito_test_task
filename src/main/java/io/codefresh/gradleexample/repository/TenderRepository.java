package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenderRepository extends JpaRepository<Tender, UUID> {
}
