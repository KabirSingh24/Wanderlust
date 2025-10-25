package com.wanderlust.WanderLust.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wanderlust.WanderLust.config.AuditField;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "listings")
public class ListEntity extends AuditField implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Column(length = 1000)
    @NotBlank(message = "Description is required")
    private String description;

    private String images_url;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonManagedReference
    private List<ReviewEntity> reviews = new ArrayList<>();

    @NotNull(message = "Price is required")
    private Double price;
    @NotBlank(message = "Location is required")
    private String location;
    @NotBlank(message = "Country is required")
    private String country;

    @Builder.Default
    private boolean active=true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonBackReference
    private UserEntity user;

}
