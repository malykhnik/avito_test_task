package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.OrganizationResponsible;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationResponsibleRepository extends JpaRepository<OrganizationResponsible, UUID> {
    Optional<OrganizationResponsible> findOrganizationResponsibleByOrganizationAndUser(Organization organization, User user);
}
