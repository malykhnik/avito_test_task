package io.codefresh.gradleexample.mapper;

import io.codefresh.gradleexample.dto.FeedbackResponseDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Feedback;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.enumerate.Status;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackMapper {
    public static FeedbackResponseDto toDto(Feedback feedback) {
        return FeedbackResponseDto.builder()
                .id(feedback.getId())
                .description(feedback.getFeedback())
                .createdAt(feedback.getCreatedAt())
                .build();
    }


    public static List<FeedbackResponseDto> toDtoList(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .map(FeedbackMapper::toDto)
                .collect(Collectors.toList());
    }
}

