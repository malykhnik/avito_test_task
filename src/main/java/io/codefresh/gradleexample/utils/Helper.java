package io.codefresh.gradleexample.utils;

import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.OrganizationResponsible;
import io.codefresh.gradleexample.entity.User;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.repository.OrganizationRepository;
import io.codefresh.gradleexample.repository.OrganizationResponsibleRepository;
import io.codefresh.gradleexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Helper {
    private final UserRepository userRepo;
    private final OrganizationResponsibleRepository responsibleRepo;
    private final OrganizationRepository organizationRepo;
    public boolean isUserResponsibleForOrganization(String username, UUID organization_id) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        Optional<Organization> organizationOptional = organizationRepo.findOrganizationById(organization_id);
        if (userOptional.isPresent() && organizationOptional.isPresent()) {
            User user = userOptional.get();
            Organization organization = organizationOptional.get();
            Optional<OrganizationResponsible> organizationResponsibleOptional =
                    responsibleRepo.findOrganizationResponsibleByOrganizationAndUser(organization, user);
            return organizationResponsibleOptional.isPresent();
        }
        throw new NotFoundUserRightsException();
    }
}
