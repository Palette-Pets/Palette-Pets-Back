package com.palette.palettepetsback.hotSpot.repository;

import com.palette.palettepetsback.hotSpot.dto.response.HotSpotRecentDTO;
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

    @Query("select new com.palette.palettepetsback.hotSpot.dto.response.HotSpotRecentDTO(hs.id, hs.member.memberId, " +
//            "hs.imgHotSpot.imgUrl, " +
            "hs.placeName, function('date_format', hs.createdAt, '%Y년 %m월 %d일'))" +
            "from HotSpot as hs order by hs.createdAt desc " +
            "LIMIT 5")
    List<HotSpotRecentDTO> findTop10ByOrderByCreatedAtDesc();
}