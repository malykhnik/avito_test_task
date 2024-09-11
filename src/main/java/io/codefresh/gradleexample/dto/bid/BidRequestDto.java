package io.codefresh.gradleexample.dto.bid;

import io.codefresh.gradleexample.enumerate.AuthorType;
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
public class BidRequestDto {
    @NotNull(message = "name can not be null")
    private String name;
    @NotNull(message = "description can not be null")
    private String description;
    @NotNull(message = "tenderId can not be null")
    private UUID tenderId;
    @NotNull(message = "authorType can not be null")
    private AuthorType authorType;
    @NotNull(message = "authorId can not be null")
    private UUID authorId;
    @NotNull(message = "organizationId can not be null")
    private UUID organizationId;

}
