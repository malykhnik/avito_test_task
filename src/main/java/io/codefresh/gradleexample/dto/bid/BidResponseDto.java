package io.codefresh.gradleexample.dto.bid;

import io.codefresh.gradleexample.enumerate.AuthorType;
import io.codefresh.gradleexample.enumerate.Status;
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
public class BidResponseDto {
    private UUID id;
    private String name;
    private Status status;
    private AuthorType authorType;
    private UUID authorId;
    private Long version;
    private LocalDateTime createdAt;
}
