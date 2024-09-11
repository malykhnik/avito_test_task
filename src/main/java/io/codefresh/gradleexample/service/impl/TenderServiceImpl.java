package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.tender.TenderEditDto;
import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.OrganizationResponsible;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.entity.User;
import io.codefresh.gradleexample.exception.NotFoundUserRights;
import io.codefresh.gradleexample.exception.TenderNotFound;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.mapper.TenderMapper;
import io.codefresh.gradleexample.repository.OrganizationRepository;
import io.codefresh.gradleexample.repository.OrganizationResponsibleRepository;
import io.codefresh.gradleexample.repository.TenderRepository;
import io.codefresh.gradleexample.repository.UserRepository;
import io.codefresh.gradleexample.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final TenderRepository tenderRepo;
    private final UserRepository userRepo;
    private final OrganizationResponsibleRepository responsibleRepo;
    private final OrganizationRepository organizationRepo;

    @Override
    public List<TenderResponseDto> getTenders() {
        List<Tender> tenders = tenderRepo.findAll();
        return TenderMapper.toDtoList(tenders);
    }

    @Override
    public TenderResponseDto saveTender(TenderRequestDto tenderRequestDto) {
        Optional<User> userOptional = userRepo.findByUsername(tenderRequestDto.getCreatorUsername());
        Optional<Organization> organizationOptional = organizationRepo.findOrganizationById(tenderRequestDto.getOrganizationId());

        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new UserNotFoundException();
        }

        return TenderMapper.toDto(tenderRepo.save(Tender.builder()
                .id(UUID.randomUUID())
                .name(tenderRequestDto.getName())
                .description(tenderRequestDto.getDescription())
                .serviceType(tenderRequestDto.getServiceType())
                .status("Created")
                .organization(organizationOptional.get())
                .creator(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));
    }

    @Override
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
        throw new NotFoundUserRights();
    }

    @Override
    public List<TenderResponseDto> getTendersByUser(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        List<Tender> responseDtoList = new ArrayList<>();
        if (userOptional.isPresent()) {
            Optional<List<Tender>> responseDtoListOptional = tenderRepo.findTendersByCreator(userOptional.get());
            if (responseDtoListOptional.isPresent()) responseDtoList = responseDtoListOptional.get();
        } else {
            throw new UserNotFoundException();
        }
        return TenderMapper.toDtoList(responseDtoList);
    }

    @Override
    public Tender getTenderById(UUID id) {
        Optional<Tender> tenderOptional = tenderRepo.findById(id);
        Tender tender;

        if (tenderOptional.isPresent()) {
            tender = tenderOptional.get();
            if (tender.getStatus().equals("Created") || tender.getStatus().equals("Closed")){
                throw new NotFoundUserRights();
            }
            if (tender.getStatus().equals("Published")) {
                return tender;
            }
            throw new NotFoundUserRights();
        }
        throw new TenderNotFound();
    }

    @Override
    public Tender getTenderByIdAndUsername(UUID id, String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        User user;
        Tender tender;
        if (userOptional.isPresent()) {
            user = userOptional.get();

            Optional<Tender> tenderOptional = tenderRepo.findTenderByIdAndCreator(id, user);
            if (tenderOptional.isPresent()) {
                tender = tenderOptional.get();
                UUID organization_id = tender.getOrganization().getId();
                if ((tender.getStatus().equals("Created") || tender.getStatus().equals("Closed")) &&
                        isUserResponsibleForOrganization(username, organization_id)) {
                    throw new NotFoundUserRights();
                }
                if (tender.getStatus().equals("Published")
                        && isUserResponsibleForOrganization(username, organization_id)) {
                    return tender;
                }
                throw new NotFoundUserRights();
            }
            throw new TenderNotFound();
        }
        throw new UserNotFoundException();
    }

    @Override
    public TenderResponseDto changeStatusOfTender(Tender tender, String status) {
        tender.setStatus(status);
        tenderRepo.save(tender);
        return TenderMapper.toDto(tender);
    }

    @Override
    public TenderResponseDto updateTender(Tender tender, TenderEditDto tenderEditDto) {
        tender.setName(tenderEditDto.getName());
        tender.setDescription(tenderEditDto.getDescription());
        tender.setServiceType(tenderEditDto.getServiceType());
        tenderRepo.save(tender);
        return TenderMapper.toDto(tender);
    }
}
