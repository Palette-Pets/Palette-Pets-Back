package com.palette.palettepetsback.report.entity;

import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Entity
@Builder
@Table(name = "member_report")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class MemberReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "report_id")
    private Long reportId;

    @ManyToOne
    @JoinColumn (name = "member_id", referencedColumnName = "member_id")
    private Member memberId;

    @ManyToOne
    @JoinColumn (name = "reported_id", referencedColumnName = "member_id")
    private Member reportedMemberId;

    @Column (name = "report_reason")
    private String reportReason;

    @Column (name = "report_content")
    private String reportContent;

    @Column (name = "report_count")
    private int reportCount;

    @Column (name = "report_date")
    private Date reportDate;

    @PrePersist
    public void prePersist() {
        this.reportCount = 1;
        this.reportDate = new Date();
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

}

