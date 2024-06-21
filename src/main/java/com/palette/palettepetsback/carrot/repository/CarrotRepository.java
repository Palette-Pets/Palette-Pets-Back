package com.palette.palettepetsback.carrot.repository;

import com.palette.palettepetsback.carrot.domain.Carrot;
import com.palette.palettepetsback.carrot.dto.CarrotRecentDTO;
import com.palette.palettepetsback.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarrotRepository extends JpaRepository<Carrot, Long> {
    @Modifying
    @Query("update Carrot c set c.carrotView = c.carrotView + 1 where c.carrotId = :id")
    int updateView(Long id);

    List<Carrot> findByMember (Member member);

    List<Carrot> findByCarrotTitleContainingOrCarrotContentContaining(String title, String content);



    @Query("select new com.palette.palettepetsback.carrot.dto.CarrotRecentDTO(c.carrotId, c.carrotTitle, " +
            "c.carrotImage, c.carrotLike, c.carrotView) from Carrot c order by c.carrotId desc " +
            "LIMIT 5")
    List<CarrotRecentDTO> findRecentCarrot();
}
