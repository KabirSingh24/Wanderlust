package com.wanderlust.WanderLust.repo;

import com.wanderlust.WanderLust.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepo extends JpaRepository<ReviewEntity,Long> {

}
