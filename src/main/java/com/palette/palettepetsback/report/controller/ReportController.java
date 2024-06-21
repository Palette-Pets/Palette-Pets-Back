package com.palette.palettepetsback.report.controller;

import com.palette.palettepetsback.Article.articleWrite.service.ArticleWriteService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.report.dto.MemberReportAddRequest;
import com.palette.palettepetsback.report.entity.MemberReport;
import com.palette.palettepetsback.report.service.MemberReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final MemberReportService memberReportService;
    private final ArticleWriteService articleWriteService;
    // 신고하는 사람의 Id
    @GetMapping("/getMemberId")
    public ResponseEntity<Long> getMemberId(@JwtAuth AuthInfoDto authInfoDto) {
        log.info("authInfoDto: {}", authInfoDto);
        log.info("...");
        return ResponseEntity.ok().body(authInfoDto.getMemberId());
    }

    @PostMapping("/addReport")
    public ResponseEntity<String> addReport(@RequestBody MemberReportAddRequest memberReportAddRequest) {
        log.info("MemberReportAddRequest: {}", memberReportAddRequest);

        //신고 등록
        memberReportService.addReport(memberReportAddRequest);
        //신고당한 게시글 신고수 증가
        articleWriteService.incrementReportCount(memberReportAddRequest.getArticleId());
        return ResponseEntity.ok().body("신고가 완료되었습니다.");
    }

}
