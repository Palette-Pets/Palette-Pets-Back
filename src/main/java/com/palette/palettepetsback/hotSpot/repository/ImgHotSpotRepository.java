package com.palette.palettepetsback.hotSpot.repository;

import com.palette.palettepetsback.hotSpot.entity.ImgHotSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImgHotSpotRepository extends JpaRepository<ImgHotSpot, Long> {

    @Query("select ihs from ImgHotSpot ihs where ihs.hotSpot.id = :hotSpotId")
    List<ImgHotSpot> findAllByHotSpotId(@Param("hotSpotId") Long hotSpotId);
}