package com.palette.palettepetsback.member.controller;

import com.palette.palettepetsback.config.security.CustomUserDetails;
import com.palette.palettepetsback.member.dto.FollowResponse;
import com.palette.palettepetsback.member.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;


    private static Long getMemberId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMember().getMemberId();
        return memberId;
    }

    @PostMapping("member/{nickname}")
    public ResponseEntity<String> follow(@PathVariable("nickname") String nickname) {
        
        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            followService.follow(nickname, memberId);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

    }

    @DeleteMapping("member/{nickname}")
    public ResponseEntity<String> unfollow(@PathVariable("nickname") String nickname) {
        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            followService.unfollow(nickname, memberId);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }
    }
    @GetMapping("member/follower/{nickname}")
    public ResponseEntity<List<FollowResponse>> getFollowers(@PathVariable("nickname") String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            List<FollowResponse> followerList = followService.getFollowerList(nickname, memberId);
            return ResponseEntity.ok(followerList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("member/following/{nickname}")
    public ResponseEntity<List<FollowResponse>> getFolloweings(@PathVariable("nickname") String nickname) {
        System.out.println("nickname = " + nickname);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            List<FollowResponse> followerList = followService.getFollowingList(nickname, memberId);
            System.out.println("followerList = " + followerList);
            return ResponseEntity.ok(followerList);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
