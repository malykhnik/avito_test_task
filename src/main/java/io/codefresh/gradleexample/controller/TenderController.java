package io.codefresh.gradleexample.controller;

import io.codefresh.gradleexample.dto.ErrorDto;
import io.codefresh.gradleexample.dto.tender.TenderEditDto;
import io.codefresh.gradleexample.dto.tender.TenderRequestDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.enumerate.Status;
import io.codefresh.gradleexample.exception.NotFoundUserRightsException;
import io.codefresh.gradleexample.exception.OrganizationNotFoundException;
import io.codefresh.gradleexample.exception.TenderNotFoundException;
import io.codefresh.gradleexample.exception.UserNotFoundException;
import io.codefresh.gradleexample.service.TenderService;
import io.codefresh.gradleexample.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenders")
@RequiredArgsConstructor
public class TenderController {
    private final TenderService tenderService;
    private final Helper helper;

    @GetMapping()
    public ResponseEntity<?> getTenders(@RequestParam(defaultValue = "5", required = false) int limit,
                                        @RequestParam(defaultValue = "0", required = false) int offset,
                                        @RequestParam(required = false) List<String> service_type) {
        if (limit <= 0 || offset < 0) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("limit or offset less then 0"));
        }

        List<String> serviceTypes;
        if (service_type == null) serviceTypes = new ArrayList<>();
        else serviceTypes = service_type;

        Page<TenderResponseDto> tenders = tenderService.getTenders(serviceTypes, limit, offset);

        return ResponseEntity.ok(tenders.getContent());

    }

    @PostMapping("/new")
    public ResponseEntity<?> createTender(@Valid @RequestBody TenderRequestDto tenderRequestDto) {
        try {
            if (!helper.isUserResponsibleForOrganization(tenderRequestDto.getCreatorUsername(), tenderRequestDto.getOrganizationId())) {
                return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (OrganizationNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Organization not found"));
        }

        return ResponseEntity.ok(tenderService.saveTender(tenderRequestDto));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getTenderNyUsername(@RequestParam(defaultValue = "5", required = false) int limit,
                                                 @RequestParam(defaultValue = "0", required = false) int offset,
                                                 @RequestParam String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(400).body(null);
        if (limit <= 0 || offset < 0) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("limit or offset less then 0"));
        }
        try {
            return ResponseEntity.ok(tenderService.getTendersByUser(username, limit, offset));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        }
    }

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
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }

    @PutMapping("/{tenderId}/status")
    public ResponseEntity<?> editStatusTender(@PathVariable UUID tenderId,
                                              @RequestParam Status status,
                                              @RequestParam String username) {
        if (tenderId == null || status == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parameter is empty"));
        }
        try {
            Tender tender = tenderService.getTenderByIdAndUsername(tenderId, username);
            return ResponseEntity.ok(tenderService.changeStatusOfTender(tender, status));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }

    @PatchMapping("/{tenderId}/edit")
    public ResponseEntity<?> editTender(@PathVariable UUID tenderId,
                                        @RequestParam String username,
                                        @RequestBody TenderEditDto tenderEditDto) {
        if (tenderId == null || username == null || username.isEmpty()) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            Tender tender = tenderService.getTenderByIdAndUsername(tenderId, username);
            return ResponseEntity.ok(tenderService.updateTender(tender, tenderEditDto));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }

    @PutMapping("/{tenderId}/rollback/{version}")
    public ResponseEntity<?> rollbackTender(@PathVariable UUID tenderId,
                                            @PathVariable Long version,
                                            @RequestParam String username) {
        if (username == null || username.isEmpty() || tenderId == null) {
            return ResponseEntity.status(400).body(ErrorDto.builder().reason("One or more parametr is empty"));
        }
        try {
            return ResponseEntity.ok(tenderService.rollbackToVersion(tenderId, username, version));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).body(ErrorDto.builder().reason("User not found"));
        } catch (NotFoundUserRightsException e) {
            return ResponseEntity.status(403).body(ErrorDto.builder().reason("The user is not responsible with the organization"));
        } catch (TenderNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().reason("Tender not found"));
        }
    }

}
