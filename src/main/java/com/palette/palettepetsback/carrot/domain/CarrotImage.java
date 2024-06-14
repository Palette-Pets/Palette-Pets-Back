package com.palette.palettepetsback.carrot.domain;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "carrot_image")
@NoArgsConstructor
public class CarrotImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrot_image_id")
    private Long carrotImageId;

    @ManyToOne
    @JoinColumn(name = "carrot_id", nullable = false)
    private Carrot carrotId;

    @Column(name = "carrort_image_url")
    private String carrotImageUrl;

    public void saveImg(String carrotImageUrl, Carrot carrotId) {
        this.carrotImageUrl = carrotImageUrl;
        this.carrotId = carrotId;
    }
}
