package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findOrganizationById(UUID id);
}
