package com.wanderlust.WanderLust.service;

import com.wanderlust.WanderLust.dto.ListingDto;
import com.wanderlust.WanderLust.entity.ListEntity;
import com.wanderlust.WanderLust.mapper.ListingMapper;
import com.wanderlust.WanderLust.repo.ListingRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepo listingRepo;
    private final ListingMapper listingMapper;
    private final String CACHE_NAME="lists";


    @CachePut(value = CACHE_NAME,key="#result.id")
    public ListEntity saveList(@Valid ListEntity listing) {
        return listingRepo.save(listing);
    }

    @CachePut(value = CACHE_NAME,key="#id")
    public ListEntity findByIdWithReviews(Long id) {
        return listingRepo.findByIdWithReviews(id).orElseThrow();
    }

    public void deleteById(Long id) {
        listingRepo.deleteById(id);
    }

    @Cacheable(value = "lists",key = "#id")
    public ListEntity findById(Long id) {
        return listingRepo.findById(id).orElseThrow();
    }

    @Cacheable(value="ListExist",key="#title")
    public boolean existsByTitle(@NotBlank(message = "Title is required") String title) {
        return listingRepo.existsByTitle(title);
    }
}
