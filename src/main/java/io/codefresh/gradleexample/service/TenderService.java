package io.codefresh.gradleexample.service;

import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;

import java.util.List;

public interface TenderService {
    List<TenderResponseDto> getTenders();
    TenderResponseDto saveTender(TenderRequestDto tenderRequestDto);
}
