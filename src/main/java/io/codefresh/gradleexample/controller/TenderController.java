package io.codefresh.gradleexample.controller;

import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenders")
@RequiredArgsConstructor
public class TenderController {
    private final TenderService tenderService;

    @GetMapping()
    public ResponseEntity<List<TenderResponseDto>> getTenders() {
        return ResponseEntity.ok(tenderService.getTenders());
    }

    @PostMapping("/new")
    public ResponseEntity<TenderResponseDto> createTender(@RequestBody TenderRequestDto tenderRequestDto) {

    }
}
