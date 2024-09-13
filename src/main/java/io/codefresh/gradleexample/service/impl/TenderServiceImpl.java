package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.tender.TenderEditDto;
import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.OrganizationResponsible;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.entity.User;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.exception.OrganizationNotFoundException;
import io.codefresh.gradleexample.exception.TenderNotFoundException;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.mapper.TenderMapper;
import io.codefresh.gradleexample.repository.OrganizationRepository;
import io.codefresh.gradleexample.repository.OrganizationResponsibleRepository;
import io.codefresh.gradleexample.repository.TenderRepository;
import io.codefresh.gradleexample.repository.UserRepository;
import io.codefresh.gradleexample.service.TenderService;
import io.codefresh.gradleexample.utils.Helper;
import lombok.RequiredArgsConstructor;
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
    private final Helper helper;

    @Override
    public List<TenderResponseDto> getTenders() {
        List<Tender> tenders = tenderRepo.findAll();
        return TenderMapper.toDtoList(tenders);
    }

    @Override
    public TenderResponseDto saveTender(TenderRequestDto tenderRequestDto) {
        Optional<User> userOptional = userRepo.findByUsername(tenderRequestDto.getCreatorUsername());
        Optional<Organization> organizationOptional = organizationRepo.findById(tenderRequestDto.getOrganizationId());

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new UserNotFoundException();
        }

        Organization organization;
        if (organizationOptional.isPresent()) {
            organization = organizationOptional.get();
        } else {
            throw new OrganizationNotFoundException();
        }


        return TenderMapper.toDto(tenderRepo.save(Tender.builder()
                .id(UUID.randomUUID())
                .name(tenderRequestDto.getName())
                .description(tenderRequestDto.getDescription())
                .serviceType(tenderRequestDto.getServiceType())
                .status(Status.valueOf(String.valueOf(Status.Created)))
                .version(1L)
                .organization(organization)
                .creator(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));
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
            if (tender.getStatus().equals(Status.Created) || tender.getStatus().equals(Status.Closed)) {
                throw new NotFoundUserRightsException();
            }
            if (tender.getStatus().equals(Status.Published)) {
                return tender;
            }
            throw new NotFoundUserRightsException();
        }
        throw new TenderNotFoundException();
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
                if ((tender.getStatus().equals(Status.Created) || tender.getStatus().equals(Status.Closed)) &&
                        helper.isUserResponsibleForOrganization(username, organization_id)) {
                    return tender;
                }
                if (tender.getStatus().equals(Status.Published)) {
                    return tender;
                }
                throw new NotFoundUserRightsException();
            }
            throw new TenderNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public TenderResponseDto changeStatusOfTender(Tender tender, Status status) {
        //обработка исключений уже произошла в методе getTenderByIdAndUsername
        tender.setStatus(status);
        tenderRepo.save(tender);
        return TenderMapper.toDto(tender);
    }

    @Override
    public TenderResponseDto updateTender(Tender tender, TenderEditDto tenderEditDto) {
        Optional<User> userOptional = userRepo.findByUsername(tender.getCreator().getUsername());
        if (userOptional.isPresent()) {
            User user =  userOptional.get();
            UUID organization_id = tender.getOrganization().getId();
            if ((tender.getStatus().equals(Status.Created) || tender.getStatus().equals(Status.Closed)) &&
                    helper.isUserResponsibleForOrganization(user.getUsername(), organization_id)) {
                Tender tenderUpdate = Tender.builder()
                        .id(UUID.randomUUID())
                        .name(tender.getName())
                        .description(tender.getDescription())
                        .serviceType(tender.getServiceType())
                        .status(tender.getStatus())
                        .version(tender.getVersion() + 1)
                        .organization(tender.getOrganization())
                        .creator(tender.getCreator())
                        .createdAt(tender.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();
                if (tenderEditDto.getName() != null && !tenderEditDto.getName().isEmpty())
                    tenderUpdate.setName(tenderEditDto.getName());
                if (tenderEditDto.getDescription() != null && !tenderEditDto.getDescription().isEmpty())
                    tenderUpdate.setDescription(tenderEditDto.getDescription());
                if (tenderEditDto.getServiceType() != null && !String.valueOf(tenderEditDto.getServiceType()).isEmpty())
                    tenderUpdate.setServiceType(tenderEditDto.getServiceType());
                tenderRepo.save(tenderUpdate);
                return TenderMapper.toDto(tenderUpdate);
            }
            throw new NotFoundUserRightsException();
        }
        throw new UserNotFoundException();

    }

    @Override
    public Tender getTenderByIdAndUsernameAndVersion(UUID id, String username, Long version) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            Optional<Tender> tenderOptional = tenderRepo.findTenderByIdAndCreatorAndVersion(id, user, version);
            if (tenderOptional.isPresent()) {
                return tenderOptional.get();
            }
            throw new TenderNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public TenderResponseDto rollbackToVersion(UUID id, String username, Long version) {
        //обработка исключений уже произошла в методе getTenderByIdAndUsername
        Tender tender = getTenderByIdAndUsername(id, username);
        Tender tenderVersion = getTenderByIdAndUsernameAndVersion(id, username, version);

        tender.setName(tenderVersion.getName());
        tender.setServiceType(tenderVersion.getServiceType());
        tender.setDescription(tenderVersion.getDescription());
        tender.setStatus(tenderVersion.getStatus());
        tender.setVersion(tender.getVersion() + 1);

        tenderRepo.save(tender);

        return TenderMapper.toDto(tender);
    }
}
