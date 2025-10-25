package com.wanderlust.WanderLust.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wanderlust.WanderLust.config.AuditField;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class ReviewEntity extends AuditField implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment not be empty")
    private String comment;

    @Min(value = 1, message = "Review must be at least 1")
    @Max(value = 5, message = "Review cannot exceed 5")
    private int reviews;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listings_id")
    @JsonBackReference
    private ListEntity listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private UserEntity user;

}
