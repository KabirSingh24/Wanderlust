package com.wanderlust.WanderLust.mapper;

import com.wanderlust.WanderLust.dto.ListingDto;
import com.wanderlust.WanderLust.entity.ListEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface ListingMapper {

    ListingDto toDto(ListEntity listEntity);
    ListEntity toEntity(ListingDto listingDto);
}
