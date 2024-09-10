package io.codefresh.gradleexample.mapper;

import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Tender;

import java.util.List;
import java.util.stream.Collectors;

public class TenderMapper {

    public static TenderResponseDto toDto(Tender tender) {
        return TenderResponseDto.builder()
                .id(tender.getId())
                .name(tender.getName())
                .description(tender.getDescription())
                .status(tender.getStatus())
                .serviceType(tender.getServiceType())
                .organization_name(tender.getOrganization().getName())
                .creator_name(tender.getCreator().getUsername())
                .build();
    }

    public static List<TenderResponseDto> toDtoList(List<Tender> tenders) {
        return tenders.stream()
                .map(TenderMapper::toDto)
                .collect(Collectors.toList());
    }
}
