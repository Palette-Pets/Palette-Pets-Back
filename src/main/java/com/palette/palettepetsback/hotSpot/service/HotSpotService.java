package com.palette.palettepetsback.hotSpot.service;

import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import com.palette.palettepetsback.config.exceptions.NoMemberExistException;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotAddRequest;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotStarPointAddRequest;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotUpdateRequest;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotListResponse;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotResponse;
import com.palette.palettepetsback.hotSpot.dto.response.ImgHotSpotResponse;
import com.palette.palettepetsback.hotSpot.entity.HotSpot;
import com.palette.palettepetsback.hotSpot.entity.HotSpotStarPoint;
import com.palette.palettepetsback.hotSpot.entity.ImgHotSpot;
import com.palette.palettepetsback.hotSpot.repository.HotSpotRepository;
import com.palette.palettepetsback.hotSpot.repository.HotSpotStarPointRepository;
import com.palette.palettepetsback.hotSpot.repository.ImgHotSpotRepository;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotSpotService {
    private final HotSpotRepository hotSpotRepository;
    private final MemberRepository memberRepository;
    private final NCPObjectStorageService objectStorageService;
    private final ImgHotSpotRepository imgHotSpotRepository;
    private final HotSpotStarPointRepository hotSpotStarPointRepository;

    //hotspot 저장 메서드
    @CacheEvict(value = "hotSpotList", allEntries = true, cacheManager = "cacheManager")
    @Transactional
    public Long HotSpotInsert(HotSpotAddRequest dto) {

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new NoMemberExistException("존재하지 않는 회원입니다."));

        HotSpot hotSpot = HotSpot.builder()
                .lng(dto.getLng())
                .lat(dto.getLat())
                .simpleContent(dto.getSimpleContent())
                .address(dto.getAddress())
                .content(dto.getContent())
                .placeName(dto.getPlaceName())
                .member(member)
                .build();

        HotSpot saved = hotSpotRepository.save(hotSpot);

        return saved.getId();
    }

    //hotspot 업데이트 메서드
    @CacheEvict(value = "hotSpotList", allEntries = true, cacheManager = "cacheManager")
    @Transactional
    public void HotSpotUpdate(HotSpotUpdateRequest dto, MultipartFile[] files) {

        HotSpot hotSpot = hotSpotRepository.findById(dto.getHotSpotId())
                .orElseThrow(() -> new RuntimeException("명소 추천 게시글이 존재하지 않습니다."));

        hotSpot.updateHotSpot(dto);

        // 연결된 imgHotSpot 모두 삭제 후 새거 넣기
        // 연관 이미지 모두삭제
        List<ImgHotSpot> imgHotSpots = hotSpot.getImgHotSpots();
        for (ImgHotSpot imgHotSpot : imgHotSpots) {
            log.info("imgHotSpot Id : {}", imgHotSpot.getId());
            log.info("imgHotSpot url : {}", imgHotSpot.getImgUrl());
            deleteHotSpotImage(imgHotSpot.getId());
        }

        if (files.length > 0) {
            // 새 거 넣기
            for (MultipartFile file : files) {
                addHotSpotImage(hotSpot.getId(), file);
            }
        } else {
            log.info("수정해서 대신 넣을 file 이 없음");
        }

    }

    //hotspot 삭제 메서드
    @CacheEvict(value = "hotSpotList", allEntries = true, cacheManager = "cacheManager")
    @Transactional
    public void HotSpotDelete(Long id){
        HotSpot hotSpot = hotSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("명소 추천 게시글이 존재하지 않습니다."));

        hotSpot.changeIsDeleted();
    }

    //hotspot 리스트 쿼리 메서드
    @Cacheable(value = "hotSpotList", cacheManager = "cacheManager")
    public List<HotSpotListResponse> getAllHotSpot(){
        List<HotSpot> hotSpotList = hotSpotRepository.findHotSpotList();

        //controller로 반환할 빈 dto 리스트 생성
        List<HotSpotListResponse> list = new ArrayList<>();

        for (HotSpot hotSpot : hotSpotList) {


            Integer rating = getHotSpotAverageStarPoint(hotSpot);
            // 첫번째 사진을 대표 사진으로 가져오기
            ImgHotSpot imgHotSpot = hotSpot.getImgHotSpots().get(0);
            String imgUrl = imgHotSpot.getImgUrl();
            HotSpotListResponse dto = hotSpot.toDto(rating,imgUrl);

            list.add(dto);
        }

        return list;
    }

    // 이미지 등록 메서드
    @Transactional
    public Long addHotSpotImage(Long hotSpotId, MultipartFile img) {

        HotSpot hotSpot = hotSpotRepository.findById(hotSpotId)
                .orElseThrow(() -> new RuntimeException("명소 추천 게시글이 존재하지 않습니다."));

        // 버킷 이름과 디렉토리 경로를 명시하도록 수정 필요
        String directoryPath = "hotspot"; // 디렉토리 경로 설정
        String fileName = objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, directoryPath, img);

        // 반환된 파일 이름을 URL로 변환
        if (fileName == null) {
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }
        String imageUrl = fileName; // URL 생성 로직

        ImgHotSpot saved = imgHotSpotRepository.save(
                ImgHotSpot.builder()
                        .imgUrl(imageUrl)
                        .hotSpot(hotSpot)
                        .build()
        );

        return saved.getId();
    }

    // 이미지 삭제 메서드
    @Transactional
    public void deleteHotSpotImage(Long imgHotSpotId) {
        ImgHotSpot imgHotSpot = imgHotSpotRepository.findById(imgHotSpotId)
                .orElseThrow(() -> new RuntimeException("이미지 명소가 존재하지 않습니다."));

        log.info("[ImgHotSpot] : img 파일 s3 삭제 로직");
        objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, imgHotSpot.getImgUrl());
        imgHotSpotRepository.delete(imgHotSpot);

    }

    // 명소 이미지 리스트 쿼리
    public List<ImgHotSpotResponse> getAllImgHotSpotByHotSpotId(Long hotSpotId) {

        List<ImgHotSpot> result = imgHotSpotRepository.findAllByHotSpotId(hotSpotId);

        List<ImgHotSpotResponse> imgHotSpotDtoList = new ArrayList<>();

        for (ImgHotSpot imgHotSpot : result) {

            ImgHotSpotResponse response = ImgHotSpotResponse.builder()
                    .imgHotSpotId(imgHotSpot.getId())
                    .imgUrl(imgHotSpot.getImgUrl())
                    .build();

            imgHotSpotDtoList.add(response);
        }

        return imgHotSpotDtoList;
    }

    // 명소 글 + 이미지 쿼리 -> join ( 글 상세조회 )
    public HotSpotResponse getHotSpotWithImg(Long hotSpotId) {
        HotSpot hotSpot = hotSpotRepository.findById(hotSpotId)
                .orElseThrow(() -> new RuntimeException("hotSpot not found"));

        List<ImgHotSpot> imgHotSpots = hotSpot.getImgHotSpots();

        List<ImgHotSpotResponse> imgHotSpotDtoList = new ArrayList<>();

        for (ImgHotSpot imgHotSpot : imgHotSpots) {

            ImgHotSpotResponse response = ImgHotSpotResponse.builder()
                    .imgHotSpotId(imgHotSpot.getId())
                    .imgUrl(imgHotSpot.getImgUrl())
                    .build();

            imgHotSpotDtoList.add(response);
        }

        // 별점 계산해서 각각 게시물 하나에 별점 결과값 넣어주기 = (총 별점 / 평가자수)
        Integer rating = getHotSpotAverageStarPoint(hotSpot);

        // 엔티티 -> DTO
        return HotSpotResponse.builder()
                .hotSpotId(hotSpot.getId())
                .memberNickname(hotSpot.getMember().getMemberNickname())
                .createAt(hotSpot.getCreatedAt())
                .modifiedAt(hotSpot.getModifiedAt())
                .placeName(hotSpot.getPlaceName())
                .simpleContent(hotSpot.getSimpleContent())
                .content(hotSpot.getContent())
                .address(hotSpot.getAddress())
                .lat(hotSpot.getLat())
                .lng(hotSpot.getLng())
                .countViews(hotSpot.getCountViews())
                .rating(rating)
                .imgList(imgHotSpotDtoList)
                .build();
    }

    // 명소 글 별점 추천 생성 메서드
    @Transactional
    public boolean saveHotSpotStarPoint(HotSpotStarPointAddRequest dto) {

        HotSpot hotSpot = hotSpotRepository.findById(dto.getHotSpotId())
                .orElseThrow(() -> new RuntimeException("hotSpot not found"));
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("memberId not found"));

        // 이미 존재하는 별점이 있다면 없애고 등록
        Optional<HotSpotStarPoint> alreadyRated = hotSpotStarPointRepository.findAlreadyRated(dto.getHotSpotId(), dto.getMemberId());
        alreadyRated.ifPresent(hotSpotStarPointRepository::delete);


        // 별점 등록
        HotSpotStarPoint saved = hotSpotStarPointRepository.save(
                HotSpotStarPoint.builder()
                        .hotSpot(hotSpot)
                        .member(member)
                        .rating(dto.getRating())
                        .build()
        );

        return saved.getId() != null;
    }

    public Integer getHotSpotStarPoint(Long hotSpotId, Long memberId) {
        HotSpotStarPoint hotSpotStarPoint = hotSpotStarPointRepository.findAlreadyRated(hotSpotId, memberId)
                .orElse(HotSpotStarPoint.builder().rating(0).build());
        // 이미 평가한게 없다면 0을 반환하도록 함 -> 0은 평가 안했다는 의미
        return hotSpotStarPoint.getRating();
    }

    @CacheEvict(value = "hotSpotList", allEntries = true, cacheManager = "cacheManager")
    @Transactional
    public void plusCountView(Long hotSpotId) {
        HotSpot hotSpot = hotSpotRepository.findById(hotSpotId)
                .orElseThrow(() -> new RuntimeException("hotSpot not found"));
        hotSpot.plusCountViews();
    }

    private Integer getHotSpotAverageStarPoint(HotSpot hotSpot) {
        // 별점 계산해서 각각 게시물 하나에 별점 결과값 넣어주기 = (총 별점 / 평가자수)
        return hotSpotStarPointRepository.calculateStarPoint(hotSpot.getId());
    }

}