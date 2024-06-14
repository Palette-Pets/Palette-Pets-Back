package com.palette.palettepetsback.member.repository;

import com.palette.palettepetsback.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일 찾기
    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberId(Long memberId);
    // 이메일 중복 여부
    boolean existsByEmail(String email);
    //닉네임 중복 확인
    boolean existsByMemberNickname(String memberNickname);

    Member findByPassword(String password);

    Optional<Member> findByMemberNickname(String NickName);
    // 이메일 찾기 + 삭제 안 된 회원 찾기
    Optional<Member> findByEmailAndIsDeletedIsFalse(String email);
}
