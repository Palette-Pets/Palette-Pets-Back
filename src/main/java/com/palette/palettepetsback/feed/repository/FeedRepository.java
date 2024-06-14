package com.palette.palettepetsback.feed.repository;

import com.palette.palettepetsback.feed.entity.Feed;
import com.palette.palettepetsback.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {
    List<Feed> findByMemberId(Member memberId);

    Feed findByFeedId(Long feedId);
}
