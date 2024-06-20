package com.palette.palettepetsback.member.service;

import com.palette.palettepetsback.member.dto.FollowResponse;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.entity.Follow;
import com.palette.palettepetsback.member.repository.FollowRepository;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    public void follow(String nickname, Long followingId) {
        System.out.println("nickname = " + nickname);
        System.out.println("followingId = " + followingId);
        Optional<Member> followerMember = memberRepository.findByMemberNickname(nickname);
        Optional<Member> followingMember= memberRepository.findByMemberId(followingId);
        // 자기 자신 follow 안됨
        if (followerMember.get().getMemberId() == followingMember.get().getMemberId())
            throw new IllegalArgumentException("자기 자신을 follow할 수 없습니다.");

        if (followerMember.isPresent()) {
            Optional<Follow> Checkfollow = followRepository.findByFollowerIdAndFollowingId(followerMember.get(),followingMember.get());
            if (Checkfollow.isPresent()){
                throw new IllegalArgumentException("같은 사람을 팔로우 했습니다.");
            }
            Member follower = followerMember.get();
            Member followee = memberRepository.findById(followingId).orElseThrow();

            Follow follow = new Follow();
            follow.saveFollow(follower,followee);

            followRepository.save(follow);
        } else {
            throw new IllegalArgumentException("팔로우 하려는 유저는 없는사람입니다.");
        }
    }

    public void unfollow(String nickname, Long followeeId) {
        Optional<Member> followerMember = memberRepository.findByMemberNickname(nickname);
        Optional<Member> followingMember = memberRepository.findByMemberId(followeeId);

        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(followerMember.get(), followingMember.get());
        followRepository.delete(follow.get());
    }

    public List<FollowResponse> getFollowerList(String nickname, Long memberId) {
        Member ToMember = memberRepository.findByMemberNickname(nickname).orElseThrow();
        //Member FromMember = memberRepository.findById(memberId).orElseThrow(); //나중에 쓸수도 있어서 냅둠.

        List<Follow> list = followRepository.findByFollowerId(ToMember);
        List<FollowResponse> followerList = new ArrayList<>();

        for (Follow f : list) {
            FollowResponse followResponse =new FollowResponse();
            followResponse.setNickname(f.getFollowingId().getMemberNickname());
            followResponse.setProfile(f.getFollowingId().getMemberImage());

            followerList.add(followResponse);
        }

        return followerList;
    }

    public List<FollowResponse> getFollowingList(String nickname, Long memberId) {
        Member ToMember = memberRepository.findByMemberNickname(nickname).orElseThrow();
        //Member FromMember = memberRepository.findById(memberId).orElseThrow();

        List<Follow> list = followRepository.findByFollowingId(ToMember);
        List<FollowResponse> followingList = new ArrayList<>();

        for (Follow f : list) {
            FollowResponse followResponse = new FollowResponse();
            followResponse.setNickname(f.getFollowerId().getMemberNickname());
            followResponse.setProfile(f.getFollowerId().getMemberImage());
            followingList.add(followResponse);
        }
        return followingList;
    }

}
