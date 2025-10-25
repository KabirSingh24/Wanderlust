package com.wanderlust.WanderLust.dto;

import com.wanderlust.WanderLust.entity.ReviewEntity;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String images_url="https://images.unsplash.com/photo-1578645510447-e20b4311e3ce?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDF8fGNhbXBpbmd8ZW58MHx8MHx8fDA%3D&auto=format&fit=crop&w=800&q=60";
    private Double price;
    private String location;
    private String country;
}
