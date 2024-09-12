package io.codefresh.gradleexample.mapper;

import io.codefresh.gradleexample.dto.bid.BidRequestDto;
import io.codefresh.gradleexample.dto.bid.BidResponseDto;
import io.codefresh.gradleexample.dto.tender.TenderResponseDto;
import io.codefresh.gradleexample.entity.Bid;
import io.codefresh.gradleexample.entity.Tender;
import io.codefresh.gradleexample.enumerate.Status;

import java.util.List;
import java.util.stream.Collectors;

public class BidMapper {
    public static BidResponseDto toDto(Bid bid) {
        return BidResponseDto.builder()
                .id(bid.getId())
                .name(bid.getName())
                .status(bid.getStatus())
                .authorType(bid.getAuthorType())
                .authorId(bid.getCreator().getId())
                .version(bid.getVersion())
                .createdAt(bid.getCreatedAt())
                .build();
    }


    public static List<BidResponseDto> toDtoList(List<Bid> bids) {
        return bids.stream()
                .map(BidMapper::toDto)
                .collect(Collectors.toList());
    }
}
