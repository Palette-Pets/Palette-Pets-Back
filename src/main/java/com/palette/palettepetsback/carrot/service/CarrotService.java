package com.palette.palettepetsback.carrot.service;

import com.palette.palettepetsback.Article.articleView.DTO.PageableDTO;
import com.palette.palettepetsback.carrot.domain.Carrot;
import com.palette.palettepetsback.carrot.domain.CarrotImage;
import com.palette.palettepetsback.carrot.domain.QCarrot;
import com.palette.palettepetsback.carrot.dto.CarrotRequestDTO;
import com.palette.palettepetsback.carrot.dto.CarrotResponseDTO;
import com.palette.palettepetsback.carrot.repository.CarrotImageRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrotService {
    private final CarrotRepository carrotRepository;
    private  final CarrotImageRepository carrotImageRepository;
    private  final MemberRepository memberRepository;
    private final NCPObjectStorageService objectStorageService;

    private final JPAQueryFactory jpaQueryFactory;
    private final Integer PAGE_SIZE;

    //글 등록
    @Transactional
    public Carrot create(CarrotRequestDTO dto, Long memberId) {
        //멤버 아이디 값 받아오기
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Not exist Member Data by id : : ["+memberId+"]"));

        Carrot carrotDTO = Carrot.builder()
                .member(member)
                .carrotTitle(dto.getCarrotTitle())
                .carrotContent(dto.getCarrotContent())
                .carrotTag(dto.getCarrotTag())
                .carrot_price(dto.getCarrot_price())
                .build();
        if(carrotDTO.getCarrotId()!=null)
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
    public Carrot update(Long id , CarrotRequestDTO dto) {

        //글 찾기
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : ["+id+"]"));

        //찾은 글에 수정 할 데이터 저장
        carrot.setCarrotTitle(dto.getCarrotTitle());
        carrot.setCarrotContent(dto.getCarrotContent());
        carrot.setCarrot_price(dto.getCarrot_price());
        carrot.setCarrotTag(dto.getCarrotTag());

        Carrot carrotUpdate = carrotRepository.save(carrot);

        return carrotUpdate;
    }

    //글 삭제
    @Transactional
    public Carrot delete(Long id) {
        Carrot carrot = carrotRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : ["+id+"]"));

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
        CarrotImage carrotImage = carrotImageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not exist Carrot Data by id : ["+id+"]"));
        Long carrotImageId =  carrotImage.getCarrotImageId();
        fileDelete("carrot/img" + carrotImage.getCarrotImageUrl());
        carrotImageRepository.deleteById(carrotImageId);
    }

    //리스트 출력
    @Transactional(readOnly = true)
    public List<CarrotResponseDTO> getList(PageableDTO pd) {
        int offset = (pd.getPage()-1) * PAGE_SIZE;
        QCarrot qCarrot = QCarrot.carrot;

        PathBuilder<?> entityPath = new PathBuilder<>(Carrot.class, "carrot");
        Order order = pd.getDir() ? Order.DESC : Order.ASC;
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(new OrderSpecifier(order, entityPath.get(pd.getSort())));

        String[] searchList = pd.getWhere().split(",");

        BooleanBuilder where = new BooleanBuilder();

        List<Carrot> carrots = jpaQueryFactory
                .selectFrom(qCarrot)
                .where(where)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]))
                .offset(offset).limit(PAGE_SIZE)
                .fetch();
        System.out.println("offset : " + offset);
        System.out.println("Size : "+ carrots.size());

        List<CarrotResponseDTO> carrotResponseDTOList = carrots.stream()
                .map(responseDTO -> new CarrotResponseDTO(responseDTO))
                .collect(Collectors.toList());

        return carrotResponseDTOList;
    }

    @Transactional
    public List<Carrot> test() {
        return carrotRepository.findAll();
    }
}
