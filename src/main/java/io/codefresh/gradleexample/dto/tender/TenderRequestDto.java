package io.codefresh.gradleexample.dto.tender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenderRequestDto {

    private String name;

    private String description;

    private String serviceType;

    private String status;

    private UUID organizationId;

    private String creatorUsername;
}
