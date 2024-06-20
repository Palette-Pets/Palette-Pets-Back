package com.palette.palettepetsback.hotSpot.repository;

import com.palette.palettepetsback.hotSpot.entity.HotSpotStarPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HotSpotStarPointRepository extends JpaRepository<HotSpotStarPoint, Long> {

    @Query("select sum(hsp.rating) / count(hsp.rating) from HotSpotStarPoint hsp where hsp.hotSpot.id = :hotSpotId")
    Integer calculateStarPoint(@Param("hotSpotId") Long hotSpotId);

    @Query("select hssp from HotSpotStarPoint hssp where hssp.hotSpot.id = :hotSpotId and hssp.member.memberId = :memberId")
    Optional<HotSpotStarPoint> findAlreadyRated(@Param("hotSpotId") Long hotSpotId, @Param("memberId") Long memberId);
}
