package com.palette.palettepetsback.config.security;

import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // todo - 커스텀 예외, memberRepository findByEmail 등록필요
        Member member = memberRepository.findByEmailAndIsDeletedIsFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("member not found"));
        return new CustomUserDetails(member);
    }
}
