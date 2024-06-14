package com.palette.palettepetsback.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "follow")
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Member followerId;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private Member followingId;

    @CreationTimestamp
    @Column(name = "time")
    private LocalDateTime time;

    public void saveFollow(Member followerId, Member followingId){
        this.followerId = followerId;
        this.followingId = followingId;
    }
}