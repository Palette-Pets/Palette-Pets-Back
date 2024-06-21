package com.palette.palettepetsback.report.dto;

import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.report.entity.MemberReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberReportAddRequest {

    private Long memberId;
    private Long articleId;
    private String memberNickname;
    private String reportReason;
    private String reportContent;

    public MemberReport toEntity(Member reportMember,Member reportedMember,MemberReportAddRequest memberReportAddRequest) {
        return MemberReport.builder()
                .memberId(reportMember)
                .reportedMemberId(reportedMember)
                .reportReason(memberReportAddRequest.getReportReason())
                .reportContent(memberReportAddRequest.getReportContent())
                .build();

    }
}

