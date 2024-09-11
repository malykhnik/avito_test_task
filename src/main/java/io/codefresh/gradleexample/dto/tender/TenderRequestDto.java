package io.codefresh.gradleexample.dto.tender;

import io.codefresh.gradleexample.enumerate.ServiceType;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "name can not be null")
    private String name;
    @NotNull(message = "description can not be null")
    private String description;
    @NotNull(message = "serviceType can not be null")
    private ServiceType serviceType;
    @NotNull(message = "organizationId can not be null")
    private UUID organizationId;
    @NotNull(message = "creatorUsername can not be null")
    private String creatorUsername;
}
