package com.palette.palettepetsback.notification.repository;

import com.palette.palettepetsback.notification.domain.MemberIssue;
import com.palette.palettepetsback.notification.dto.response.MemberIssueResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberIssueRepository extends JpaRepository<MemberIssue, Long> {

    @Query("select new com.palette.palettepetsback.notification.dto.response.MemberIssueResponse(" +
            "   mi.id, mi.receiver.memberId, mi.receiver.memberNickname, mi.createdAt, mi.issueContent, mi.issueCode, mi.isRead" +
            ")" +
            "from MemberIssue mi " +
            "where mi.receiver.memberId = :memberId and mi.isRead = false " +
            "order by mi.createdAt desc ")
    List<MemberIssueResponse> findAllByMemberId(@Param("memberId") Long memberId);
}
