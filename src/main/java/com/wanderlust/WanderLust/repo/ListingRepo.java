package com.wanderlust.WanderLust.repo;

import com.wanderlust.WanderLust.entity.ListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ListingRepo extends JpaRepository<ListEntity,Long> {
    @Transactional
    @Modifying
    @Query("delete from ListEntity l")
    void deleteFirstBy();

    @EntityGraph(attributePaths = {"reviews"})
    @Query("SELECT l FROM ListEntity l LEFT JOIN FETCH l.reviews WHERE l.id = :id")
    Optional<ListEntity> findByIdWithReviews(@Param("id") Long id);

    boolean existsByTitle(String title);

    @EntityGraph(attributePaths = {"reviews","user"})
    @Query("SELECT l FROM ListEntity l LEFT JOIN FETCH l.reviews WHERE l.id = :id")
    Optional<ListEntity> findByIdWithReviewsAndUser(@Param("id") Long id);

}
