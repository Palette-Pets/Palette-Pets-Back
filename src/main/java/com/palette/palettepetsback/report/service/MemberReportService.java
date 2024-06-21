package com.palette.palettepetsback.report.service;

import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.palette.palettepetsback.report.dto.MemberReportAddRequest;
import com.palette.palettepetsback.report.entity.MemberReport;
import com.palette.palettepetsback.report.repository.MemberReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReportService {

    private final MemberReportRepository memberReportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addReport(MemberReportAddRequest memberReportAddRequest) {
        // 신고 하는 사람 Member
        Member reportMember = memberRepository.findById(memberReportAddRequest.getMemberId())
                .orElseThrow(() -> new RuntimeException("member not found"));
        //신고 당하는 사람 Member 찾기
        Member reportedMember = memberRepository.findByMemberNickname(memberReportAddRequest.getMemberNickname())
                .orElseThrow(() -> new RuntimeException("member not found"));

        //isReported(reportedMember);

        memberReportRepository.save(memberReportAddRequest.toEntity(reportMember,reportedMember, memberReportAddRequest));

    }

    private void isReported(Member reportedMember) {

//        Boolean isReported = memberReportRepository.existsByReportedMemberId(reportedMember);
//        if (isReported){
//            MemberReport memberReport = memberReportRepository.findByReportedMemberId(reportedMember);
//            memberReport.increaseReportCount();
//        }
    }
}
