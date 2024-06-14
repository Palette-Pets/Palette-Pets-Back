package com.palette.palettepetsback.hotSpot.repository;

import com.palette.palettepetsback.hotSpot.dto.response.HotSpotResponse;
import com.palette.palettepetsback.hotSpot.entity.HotSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotSpotRepository extends JpaRepository<HotSpot, Long> {

    @Query("select h from HotSpot h where h.isDeleted = false order by h.createdAt desc ")
    List<HotSpot> findHotSpotList();
}