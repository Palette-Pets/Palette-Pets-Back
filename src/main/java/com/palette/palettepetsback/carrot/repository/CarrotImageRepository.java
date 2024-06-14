package com.palette.palettepetsback.carrot.repository;

import com.palette.palettepetsback.carrot.domain.CarrotImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrotImageRepository extends JpaRepository<CarrotImage, Long> {
}
