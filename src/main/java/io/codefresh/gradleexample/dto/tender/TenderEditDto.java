package io.codefresh.gradleexample.dto.tender;

import io.codefresh.gradleexample.enumerate.ServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenderEditDto {
    private String name;
    private String description;
    private ServiceType serviceType;
}
