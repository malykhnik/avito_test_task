package io.codefresh.gradleexample.repository;

import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.OrganizationResponsible;
import io.codefresh.gradleexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationResponsibleRepository extends JpaRepository<OrganizationResponsible, UUID> {
    Optional<OrganizationResponsible> findOrganizationResponsibleByOrganizationAndUser(Organization organization, User user);
    List<OrganizationResponsible> findOrganizationResponsiblesByOrganization(Organization organization);
}
