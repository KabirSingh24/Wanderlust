package com.wanderlust.WanderLust.service;

import com.wanderlust.WanderLust.entity.ListEntity;
import com.wanderlust.WanderLust.entity.ReviewEntity;
import com.wanderlust.WanderLust.repo.ListingRepo;
import com.wanderlust.WanderLust.repo.ReviewRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final ListingRepo listingRepo;

    @CachePut(value="reviews",key="#listingId")
    public void saveReview(Long listingId, ReviewEntity review) {
        review.setId(null); // ensure it's a new review
        ListEntity listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid listing Id: " + listingId));
        review.setListing(listing);
        reviewRepo.save(review);
    }

    @CacheEvict(value = "reviews",key="#reviewId")
    public void deleteReview(Long reviewId) {
        reviewRepo.deleteById(reviewId);
    }
}
