package com.palette.palettepetsback.carrot.service;

import com.palette.palettepetsback.Article.articleView.DTO.PageableDTO;
import com.palette.palettepetsback.carrot.domain.Carrot;
import com.palette.palettepetsback.carrot.domain.CarrotImage;
import com.palette.palettepetsback.carrot.domain.CarrotLike;
import com.palette.palettepetsback.carrot.domain.QCarrot;
import com.palette.palettepetsback.carrot.dto.CarrotRecentDTO;
import com.palette.palettepetsback.carrot.dto.CarrotRequestDTO;
import com.palette.palettepetsback.carrot.dto.CarrotResponseDTO;
import com.palette.palettepetsback.carrot.repository.CarrotImageRepository;
import com.palette.palettepetsback.carrot.repository.CarrotLikeRepository;
import com.palette.palettepetsback.carrot.repository.CarrotRepository;
import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrotService {
    private final CarrotRepository carrotRepository;
    private final CarrotImageRepository carrotImageRepository;
    private final CarrotLikeRepository carrotLikeRepository;
    private final MemberRepository memberRepository;
    private final NCPObjectStorageService objectStorageService;

    private final JPAQueryFactory jpaQueryFactory;
    private final Integer PAGE_SIZE;

    //글 등록
    @Transactional
    public Carrot create(CarrotRequestDTO dto, Long memberId) {
        //멤버 아이디 값 받아오기
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Not exist Member Data by id : : [" + memberId + "]"));

        Carrot carrotDTO = Carrot.builder()
                .member(member)
                .carrotTitle(dto.getCarrotTitle())
                .carrotContent(dto.getCarrotContent())
                .carrotTag(dto.getCarrotTag())
                .carrot_price(dto.getCarrot_price())
                .build();
        if (carrotDTO.getCarrotId() != null)
            return null;

        return carrotRepository.save(carrotDTO);
    }

    // 파일 저장
    @Transactional
    public String fileUpload(MultipartFile file, String dirPath) {
        return objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, dirPath, file);
    }

    //이미지 등록
    @Transactional
    public CarrotImage saveImg(String carrotImageUrl, Carrot carrot) {
        CarrotImage carrotImage = new CarrotImage();
        carrotImage.saveImg(carrotImageUrl, carrot);
        return carrotImageRepository.save(carrotImage);
    }

    //글 수정
    @Transactional
    public void update(Long id, CarrotRequestDTO dto) {

        //글 찾기
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : [" + id + "]"));

        //찾은 글에 수정 할 데이터 저장
        carrot.setCarrotTitle(dto.getCarrotTitle());
        carrot.setCarrotContent(dto.getCarrotContent());
        carrot.setCarrot_price(dto.getCarrot_price());
        carrot.setCarrotTag(dto.getCarrotTag());
        carrot.setCarrotState(dto.getCarrotState());

        carrotRepository.save(carrot);
    }

    //글 삭제
    @Transactional
    public Carrot delete(Long id) {
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : [" + id + "]"));

        carrotRepository.delete(carrot);
        return carrot;
    }

    //파일 삭제
    @Transactional
    public String fileDelete(String fileName) {
        return objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, fileName);
    }

    //이미지 삭제
    @Transactional
    public void deleteImg(Long id) {
        CarrotImage carrotImage = carrotImageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : [" + id + "]"));
        Long carrotImageId = carrotImage.getCarrotImageId();
        fileDelete("carrot/img" + carrotImage.getCarrotImageUrl());
        carrotImageRepository.deleteById(carrotImageId);
    }

    //리스트 출력
    @Transactional(readOnly = true)
    public List<CarrotResponseDTO> getList(PageableDTO pd) {
        int offset = (pd.getPage() - 1) * PAGE_SIZE;
        QCarrot qCarrot = QCarrot.carrot;

        PathBuilder<?> entityPath = new PathBuilder<>(Carrot.class, "carrot");
        Order order = pd.getDir() ? Order.DESC : Order.ASC;
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(new OrderSpecifier(order, entityPath.get(pd.getSort())));

        BooleanBuilder where = new BooleanBuilder();

        if (pd.getWhere() != null && !pd.getWhere().isEmpty()) {
            where.and(qCarrot.carrotTag.contains(pd.getWhere()));
        }

        List<Carrot> carrots = jpaQueryFactory
                .selectFrom(qCarrot)
                .where(where)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]))
                .offset(offset).limit(PAGE_SIZE)
                .fetch();
        System.out.println("offset : " + offset);
        System.out.println("Size : " + carrots.size());

        List<CarrotResponseDTO> carrotResponseDTOList = new ArrayList<>();
        for (Carrot c : carrots) {
            List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(c);

            Long memberId = c.getMember().getMemberId();
            String memberNickname = c.getMember().getMemberNickname();

            CarrotResponseDTO carrotResponseDTO = new CarrotResponseDTO();
            carrotResponseDTO.setCarrotId(c.getCarrotId());
            carrotResponseDTO.setMemberId(memberId);
            carrotResponseDTO.setMemberNickname(memberNickname);
            carrotResponseDTO.setCarrotTitle(c.getCarrotTitle());
            carrotResponseDTO.setCarrotContent(c.getCarrotContent());
            carrotResponseDTO.setCarrotPrice(c.getCarrot_price());
            carrotResponseDTO.setCarrotState(c.getCarrotState());
            carrotResponseDTO.setCarrotCreatedAt(c.getCarrot_createdAt());
            carrotResponseDTO.setCarrotTag(c.getCarrotTag());
            carrotResponseDTO.setCarrotLike(c.getCarrotLike());
            carrotResponseDTO.setCarrotView(c.getCarrotView());

            if (!carrotImage.isEmpty()) {
                carrotResponseDTO.setCarrotImg(carrotImage.get(0).getCarrotImageUrl());
            }

            carrotResponseDTOList.add(carrotResponseDTO);
        }

//        List<CarrotResponseDTO> carrotResponseDTOList = carrots.stream()
//                .map(responseDTO -> new CarrotResponseDTO(responseDTO))
//                .collect(Collectors.toList());

        return carrotResponseDTOList;
    }

    //회원 별 작성 리스트 출력
    @Transactional
    public List<CarrotResponseDTO> test(Long userId) {
        List<Carrot> carrot = carrotRepository.findAll();
        //글쓴이 아이디
        List<CarrotResponseDTO> carrotResponseDTOList = new ArrayList<>();

        for(Carrot c : carrot) {
            if(c.getMember().getMemberId().equals(userId)) {
                List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(c);

                CarrotResponseDTO carrotResponseDTO = new CarrotResponseDTO();
                carrotResponseDTO.setCarrotId(c.getCarrotId());
                carrotResponseDTO.setMemberId(c.getMember().getMemberId());
                carrotResponseDTO.setMemberNickname(c.getMember().getMemberNickname());
                carrotResponseDTO.setCarrotTitle(c.getCarrotTitle());
                carrotResponseDTO.setCarrotContent(c.getCarrotContent());
                carrotResponseDTO.setCarrotPrice(c.getCarrot_price());
                carrotResponseDTO.setCarrotCreatedAt(c.getCarrot_createdAt());
                carrotResponseDTO.setCarrotTag(c.getCarrotTag());
                carrotResponseDTO.setCarrotLike(c.getCarrotLike());
                carrotResponseDTO.setCarrotView(c.getCarrotView());
                carrotResponseDTO.setCarrotState(c.getCarrotState());
                carrotResponseDTOList.add(carrotResponseDTO);

                if(!carrotImage.isEmpty()){
                    carrotResponseDTO.setCarrotImg(carrotImage.get(0).getCarrotImageUrl());
                }
            }
        }
            return carrotResponseDTOList;
    }

    //조회수 증가
    @Transactional
    public int updateView(Long id) {
        return carrotRepository.updateView(id);
    }

    //상세 출력
    @Transactional
    public CarrotResponseDTO listDetail(Long id) {
        Optional<Carrot> carrotId = carrotRepository.findById(id);
        Carrot carrot = carrotId.get();

        List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(carrot);
        List<String> imgList = new ArrayList<>();
        for (CarrotImage c : carrotImage) {
            imgList.add(c.getCarrotImageUrl());
        }
        System.out.println("imgList.get(0) = " + imgList.get(0));
        return CarrotResponseDTO.builder()
                .carrotId(carrot.getCarrotId())
                .memberId(carrot.getMember().getMemberId())
                .memberNickname(carrot.getMember().getMemberNickname())
                .carrotTitle(carrot.getCarrotTitle())
                .carrotContent(carrot.getCarrotContent())
                .carrotPrice(carrot.getCarrot_price())
                .imgList(imgList)
                .carrotCreatedAt(carrot.getCarrot_createdAt())
                .carrotTag(carrot.getCarrotTag())
                .carrotLike(carrot.getCarrotLike())
                .carrotView(carrot.getCarrotView())
                .carrotState(carrot.getCarrotState())
                .build();
    }


    public String like(Long carrotId, Long memberId) {
        Optional<Carrot> carrot= carrotRepository.findById(carrotId);
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        Optional<CarrotLike> carrotLike = carrotLikeRepository.findByCarrotIdAndMember(carrot.get(),member.get());

        if(carrotLike.isPresent()){
            carrotLikeRepository.delete(carrotLike.get());

            carrot.get().like(-1); //총 좋아요 개수 카운트
            carrotRepository.save(carrot.get());
            return "좋아요 취소";
        }else{
            CarrotLike like = new CarrotLike();
            like.save(member.get(),carrot.get());
            carrotLikeRepository.save(like);

            carrot.get().like(+1); //총 좋아요 개수 카운트
            carrotRepository.save(carrot.get());
            return "좋아요 완료";
        }
    }
    public List<CarrotResponseDTO> getLike(Long memberId){
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        List<Carrot> carrotList = carrotRepository.findByMember(member.get());

        List<CarrotResponseDTO> carrotResponseDTOList = new ArrayList<>();
        for (Carrot c : carrotList) {
            List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(c);

            String memberNickname = c.getMember().getMemberNickname();
            CarrotResponseDTO carrotResponseDTO = new CarrotResponseDTO();
            carrotResponseDTO.setCarrotId(c.getCarrotId());
            carrotResponseDTO.setCarrotTitle(c.getCarrotTitle());
            carrotResponseDTO.setCarrotContent(c.getCarrotContent());
            carrotResponseDTO.setCarrotPrice(c.getCarrot_price());
            carrotResponseDTO.setCarrotCreatedAt(c.getCarrot_createdAt());
            carrotResponseDTO.setMemberNickname(memberNickname); // 이건 닉네임임
            carrotResponseDTO.setMemberId(memberId);
            carrotResponseDTO.setCarrotTag(c.getCarrotTag());
            carrotResponseDTO.setCarrotLike(c.getCarrotLike());
            carrotResponseDTO.setCarrotView(c.getCarrotView());

            if(!carrotImage.isEmpty()){
                carrotResponseDTO.setCarrotImg(carrotImage.get(0).getCarrotImageUrl());
            }

            carrotResponseDTOList.add(carrotResponseDTO);
        }
        return carrotResponseDTOList;
    }

    //검색 기능
    public List<CarrotResponseDTO> searchCarrots(String keyword) {
        List<Carrot> carrotList = carrotRepository.findByCarrotTitleContainingOrCarrotContentContaining(keyword, keyword);
        List<CarrotResponseDTO> carrotResponseDTOList = new ArrayList<>();
        for (Carrot c : carrotList) {
            List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(c);

            Long memberId = c.getMember().getMemberId();
            String memberNickname = c.getMember().getMemberNickname();
            CarrotResponseDTO carrotResponseDTO=new CarrotResponseDTO();
            carrotResponseDTO.setCarrotId(c.getCarrotId());
            carrotResponseDTO.setMemberId(memberId);
            carrotResponseDTO.setMemberNickname(memberNickname);
            carrotResponseDTO.setCarrotTitle(c.getCarrotTitle());
            carrotResponseDTO.setCarrotContent(c.getCarrotContent());
            carrotResponseDTO.setCarrotPrice(c.getCarrot_price());
            carrotResponseDTO.setCarrotCreatedAt(c.getCarrot_createdAt());
            carrotResponseDTO.setCarrotTag(c.getCarrotTag());
            carrotResponseDTO.setCarrotLike(c.getCarrotLike());
            carrotResponseDTO.setCarrotView(c.getCarrotView());

            if(!carrotImage.isEmpty()){
                carrotResponseDTO.setCarrotImg(carrotImage.get(0).getCarrotImageUrl());
            }

            carrotResponseDTOList.add(carrotResponseDTO);
        }
        return carrotResponseDTOList;
    }

    //글쓴이와 로그인 사용자 확인
    public Long findId(Long id) {
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : ["+id+"]"));

        return carrot.getMember().getMemberId();
    }
    public List<CarrotRecentDTO> getRecentList(){
        return carrotRepository.findRecentCarrot();
    }
    //상태 변경 기능
    public void state(Long id, int carrotState) {
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : ["+id+"]"));

        carrot.setCarrotState(carrotState);

        carrotRepository.save(carrot);
    }

    //최신순 리스트 출력
    public List<CarrotResponseDTO> recentList(int page) {
        List<Carrot> carrot = carrotRepository.findAll();
        QCarrot qCarrot = QCarrot.carrot;

        List<Carrot> carrots = jpaQueryFactory
                .selectFrom(qCarrot)
                .orderBy(qCarrot.carrot_createdAt.desc())
                .limit(page)
                .fetch();

        List<CarrotResponseDTO> carrotResponseDTOList=new ArrayList<>();

        for(Carrot c : carrots) {
            List<CarrotImage> carrotImage = carrotImageRepository.findByCarrotId(c);

            Long memberId = c.getMember().getMemberId();
            String memberNickname = c.getMember().getMemberNickname();
            CarrotResponseDTO carrotResponseDTO=new CarrotResponseDTO();
            carrotResponseDTO.setCarrotId(c.getCarrotId());
            carrotResponseDTO.setMemberId(memberId);
            carrotResponseDTO.setMemberNickname(memberNickname);
            carrotResponseDTO.setCarrotTitle(c.getCarrotTitle());
            carrotResponseDTO.setCarrotContent(c.getCarrotContent());
            carrotResponseDTO.setCarrotPrice(c.getCarrot_price());
            carrotResponseDTO.setCarrotCreatedAt(c.getCarrot_createdAt());
            carrotResponseDTO.setCarrotTag(c.getCarrotTag());
            carrotResponseDTO.setCarrotLike(c.getCarrotLike());
            carrotResponseDTO.setCarrotView(c.getCarrotView());

            if(!carrotImage.isEmpty()){
                carrotResponseDTO.setCarrotImg(carrotImage.get(0).getCarrotImageUrl());
            }

            carrotResponseDTOList.add(carrotResponseDTO);
        }
        return carrotResponseDTOList;
    }
}
