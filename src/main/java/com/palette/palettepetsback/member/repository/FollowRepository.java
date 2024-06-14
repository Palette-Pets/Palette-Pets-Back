package com.palette.palettepetsback.member.repository;

import com.palette.palettepetsback.member.entity.Follow;
import com.palette.palettepetsback.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Member followerId, Member followingId);
    List<Follow> findByFollowingId(Member from_user);
    List<Follow> findByFollowerId(Member to_user);

}
