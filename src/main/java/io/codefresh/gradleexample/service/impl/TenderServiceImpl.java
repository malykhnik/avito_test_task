package io.codefresh.gradleexample.service.impl;

import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Organization;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.mapper.TenderMapper;
import io.codefresh.gradleexample.repository.TenderRepository;
import io.codefresh.gradleexample.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenderServiceImpl implements TenderService {
    private final TenderRepository tenderRepo;
    @Override
    public List<TenderResponseDto> getTenders() {
        List<Tender> tenders = tenderRepo.findAll();
        return TenderMapper.toDtoList(tenders);
    }

    @Override
    public TenderResponseDto saveTender(TenderRequestDto tenderRequestDto) {
        ///////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //// СОЗДАВАТЬ МОЖЕТ ТОЛЬКО ЧЕЛ ИХ КОМПАНИИ УЧЕСТЬ!!!!!!!!!!!!!
        Optional<Organization> organization =
        Tender tender = Tender.builder()
                .id(UUID.randomUUID())
                .name(tenderRequestDto.getName())
                .description(tenderRequestDto.getDescription())
                .serviceType(tenderRequestDto.getServiceType())
                .status(tenderRequestDto.getStatus())
                .organization(tenderRequestDto.getOrganizationId())
                .build();
        return tenderRepo.save();
    }
}
