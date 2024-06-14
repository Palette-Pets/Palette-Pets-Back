package com.palette.palettepetsback.hotSpot.repository;

import com.palette.palettepetsback.hotSpot.entity.HotSpotStarPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotSpotStarPointRepository extends JpaRepository<HotSpotStarPoint, Long> {

    @Query("select sum(hsp.rating) / count(hsp.rating) from HotSpotStarPoint hsp where hsp.hotSpot.id = :hotSpotId")
    Integer calculateStarPoint(@Param("hotSpotId") Long hotSpotId);
}
