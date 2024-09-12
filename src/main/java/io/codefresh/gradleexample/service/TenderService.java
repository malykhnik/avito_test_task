package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.tender.TenderEditDto;
import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.enumerate.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenderService {
    List<TenderResponseDto> getTenders();

    TenderResponseDto saveTender(TenderRequestDto tenderRequestDto);


    List<TenderResponseDto> getTendersByUser(String username);
    Tender getTenderById(UUID id);
    Tender getTenderByIdAndUsername(UUID id, String username);
    Tender getTenderByIdAndUsernameAndVersion(UUID id, String username, Long version);
    TenderResponseDto changeStatusOfTender(Tender tender, Status status);
    TenderResponseDto updateTender(Tender tender, TenderEditDto tenderEditDto);
    TenderResponseDto rollbackToVersion(UUID id, String username, Long version);
}
