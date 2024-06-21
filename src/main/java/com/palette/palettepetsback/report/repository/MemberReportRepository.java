package com.palette.palettepetsback.report.repository;

import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.report.entity.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    Boolean existsByReportedMemberId(Member reportedMember);


    MemberReport findByReportedMemberId(Member reportedMember);
}
