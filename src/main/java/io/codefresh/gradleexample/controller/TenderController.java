package io.codefresh.gradleexample.controller;

import io.codefresh.gradleexample.dto.ErrorDto;
import io.codefresh.gradleexample.dto.tender.TenderEditDto;
import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.exception.NotFoundUserRights;
import io.codefresh.gradleexample.exception.TenderNotFound;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.service.TenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenders")
@RequiredArgsConstructor
public class TenderController {
    private final TenderService tenderService;

    @GetMapping()
    public ResponseEntity<List<TenderResponseDto>> getTenders() {
        return ResponseEntity.ok(tenderService.getTenders());

        // ДОБАВИТЬ ОБРАБОТКУ НЕВЕРНОГО ФОРМАТА ЗАПРОСА
    }

    @PostMapping("/new")
    public ResponseEntity<?> createTender(@Valid @RequestBody TenderRequestDto tenderRequestDto) {
        try {
            if (!tenderService.isUserResponsibleForOrganization(tenderRequestDto.getCreatorUsername(), tenderRequestDto.getOrganizationId())) {
                return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        }

        return ResponseEntity.ok(tenderService.saveTender(tenderRequestDto));
    }

    @GetMapping("/my/{username}")
    public ResponseEntity<?> getTenderNyUsername(@PathVariable String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(400).body(null);
        try {
            List<TenderResponseDto> tenderResponseDtos = tenderService.getTendersByUser(username);
            return ResponseEntity.ok(tenderResponseDtos);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        }
    }

    // параметр необязательный проверить(required)!!!!!!!!!!!!!!!
    @GetMapping("/{tenderId}/status")
    public ResponseEntity<?> getCurrentStatus(@PathVariable UUID tenderId,
                                              @RequestParam(required = false) String username) {
        if (tenderId == null) return ResponseEntity.status(400).body(null);
        try {
            Tender tender;
            if (username.isEmpty()) {
                tender = tenderService.getTenderById(tenderId);
            } else {
                tender = tenderService.getTenderByIdAndUsername(tenderId, username);
            }
            return ResponseEntity.ok(tender.getStatus());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRights e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFound e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }

    @PutMapping("/{tenderId}/status")
    public ResponseEntity<?> editStatusTender(@PathVariable UUID tenderId,
                                        @RequestParam String status,
                                        @RequestParam String username) {
        if (tenderId == null || status.isEmpty() || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            Tender tender = tenderService.getTenderByIdAndUsername(tenderId, username);
            return ResponseEntity.ok(tenderService.changeStatusOfTender(tender, status));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRights e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFound e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }


    // ПРОВЕРИТЬ ЧТО БУДЕТ ЕСЛИ НИЧЕ НЕ ПЕРЕДАТЬ В ПАРАМЕТРЫ И В ТЕЛО
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @PatchMapping("/{tenderId}/status")
    public ResponseEntity<?> editTender(@PathVariable UUID tenderId,
                                        @RequestParam String username,
                                        @RequestBody TenderEditDto tenderEditDto) {
        if (tenderId == null || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            Tender tender = tenderService.getTenderByIdAndUsername(tenderId, username);
            return ResponseEntity.ok(tenderService.updateTender(tender, tenderEditDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRights e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFound e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }



}
