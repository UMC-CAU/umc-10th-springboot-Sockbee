package com.example.umc10th.domain.store.entity;

import com.example.umc10th.domain.mission.entity.Mission;
import com.example.umc10th.domain.review.entity.Review;
import com.example.umc10th.domain.store.enums.FoodTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_tag", nullable = false)
    private FoodTag foodTag;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "zip_code", nullable = false, length = 5)
    private String zipCode;

    @Column(name = "address_main", nullable = false, length = 255)
    private String addressMain;

    @Column(name = "address_detail", nullable = false, length = 255)
    private String addressDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_id", nullable = false)
    private Dong dong;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Mission> missions = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
}
