package io.codefresh.gradleexample.dto.tender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenderResponseDto {
    private UUID id;

    private String name;

    private String description;

    private String status;

    private String serviceType;

    private Long version;

    private LocalDateTime createdAt;
}
