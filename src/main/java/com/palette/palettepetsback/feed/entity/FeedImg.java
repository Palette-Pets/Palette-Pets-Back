package com.palette.palettepetsback.feed.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "feed_img")
public class FeedImg {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_img_id")
    private Long feedImgId;

    private String img;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    public void saveImg(String img,Feed feed) {
        this.img = img;
        this.feed = feed;
    }
}
