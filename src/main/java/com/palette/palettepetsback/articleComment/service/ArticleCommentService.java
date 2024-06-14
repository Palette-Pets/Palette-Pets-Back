package com.palette.palettepetsback.articleComment.service;


import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
import com.palette.palettepetsback.Article.exception.type.AuthInfoDtoNotEqualsException;
import com.palette.palettepetsback.Article.exception.type.CommentNotFoundException;
import com.palette.palettepetsback.articleComment.dto.request.ArticleCommentAddRequest;
import com.palette.palettepetsback.articleComment.dto.response.ArticleCommentListResponse;
import com.palette.palettepetsback.articleComment.entity.ArticleComment;
import com.palette.palettepetsback.articleComment.entity.QArticleComment;
import com.palette.palettepetsback.articleComment.repository.ArticleCommentRepository;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.palette.palettepetsback.articleComment.dto.response.ArticleCommentListResponse.convertCommentToDto;

@Service
@Transactional(readOnly = true)// 트랜잭션 걸어서 JPA가 제대로 돌아가게  함 ->  읽기만 가능 수정할거면 메서드에 각각 트랜잭션 걸기
@Slf4j
@RequiredArgsConstructor
public class ArticleCommentService {
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;

//    @Transactional(readOnly = true)
//    public List<ArticleCommentDto> comments(Article article) {
//        return articleCommentRepository.findByArticle(article) //댓글 엔티티 목록 조회
//                .stream() //댓글 엔티티 목록을 스트림으로 변환
//                .map(ArticleCommentDto::createArticleCommentDto)//엔티티를 DTO로 매핑
//                .collect(Collectors.toList()); //스트림을 리스트로 변환
//    }

    //댓글 조회
    @Transactional
    public List<ArticleCommentListResponse> comments(Long articleId) {

        QArticleComment qArticleComment = QArticleComment.articleComment;


        BooleanBuilder where = new BooleanBuilder();
        where.and(qArticleComment.article.isDeleted.eq(false));
        where.and(qArticleComment.article.articleId.eq(articleId));

        List<ArticleComment> articleComments =
                jpaQueryFactory.selectFrom(qArticleComment)
                .leftJoin(qArticleComment.parentId)
                .fetchJoin()
                .where(where)
                .orderBy(
                        qArticleComment.parentId.articleCommentId.asc().nullsFirst(),
                        qArticleComment.createdAt.asc()
                ).fetch();

        List<ArticleCommentListResponse> result = new ArrayList<>();
        Map<Long, ArticleCommentListResponse> map = new HashMap<>();
        articleComments.stream().forEach(c -> {
            ArticleCommentListResponse dto = convertCommentToDto(c);
            map.put(dto.getArticleCommentId(), dto);
            if(c.getParentId() != null) map.get(c.getParentId().getArticleCommentId()).getChildren().add(dto);
            else result.add(dto);
        });


        return result;
    }


    //댓글 작성
    @Transactional
    public ArticleComment create(ArticleCommentAddRequest dto) {

        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패" + "대상 게시글이 없습니다."));

        Member member = memberRepository.findById(dto.getCreatedWho())
                .orElseThrow(() -> new RuntimeException("member not found"));

        ArticleComment parentComment = null;

        if (dto.getParentId() != 0) {
            parentComment = articleCommentRepository.findById((long) dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패" + "부모 댓글이 없습니다."));
        }

        return articleCommentRepository.save(dto.toEntity(article,member, parentComment));
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long id, Member member) {
        ArticleComment articleComment = articleCommentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);

        validateDeleteComment(articleComment,member);
        articleCommentRepository.delete(articleComment);
    }

    private void validateDeleteComment(ArticleComment articleComment, Member member) {
        if(!articleComment.isOwnComment(member)){
            throw new AuthInfoDtoNotEqualsException();
        }
    }


//@Transactional
//public ArticleCommentDto create(Long articleId, ArticleCommentDto dto) {
//    Article article = articleRepository.findById(articleId)
//            .orElseThrow(()->new IllegalArgumentException("댓글 생성 실패" + "대상 게시글이 없습니다."));
//    ArticleComment articleComment = ArticleComment.createComment(dto,article,null);
//    ArticleComment created = articleCommentRepository.save(articleComment);
//    return ArticleCommentDto.createArticleCommentDto(created);
//}


    //부모 댓글의 ref값을 기반으로 자식 댓글의 ref값을 계산
//    private int calculateChildRef(int parentRef) {
//        return parentRef * 10 + 1;
//    }
//
//    //댓글 수정
//    @Transactional
//    public ArticleCommentRequestDto update(Long articleCommentId, ArticleCommentRequestDto dto) {
//        //댓글 조회
//        ArticleComment target = articleCommentRepository.findById(articleCommentId)
//                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을수 없습니다"));
//        //댓글 내용 업데이트
//        target.setContent(dto.getContent());
//        //변경 사항저장
//        ArticleComment updatedComment = articleCommentRepository.save(target);
//        //DTO로 변환하여 반환
//        return ArticleCommentRequestDto.createArticleCommentDto(updatedComment);
//    }
//
//    //댓글 삭제
//    public ArticleComment delete(Long articleCommentId) {
//        //대상찾기
//        ArticleComment target = articleCommentRepository.findById(articleCommentId).orElse(null);
//        //잘못된 요청 처리하기
//        if (target == null) {
//            return null;
//        }
//        //대상 삭제하기
//        articleCommentRepository.delete(target);
//        return target;
//    }

//        @Transactional
//        public ArticleCommentDto update(Long articleCommentId,ArticleCommentDto dto){
//            //댓글 조회
//            ArticleComment target = articleCommentRepository.findById(articleCommentId)
//                    .orElseThrow(()->new IllegalArgumentException("댓글을 찾을 수 없습니다."));
//            // 댓글 내용 업데이트
//            target.setContent(dto.getContent());
//            //변경 사항 저장
//            ArticleComment updatedComment=articleCommentRepository.save(target);
//            //DTO로 변환하여 반환
//            return ArticleCommentDto.createArticleCommentDto(updatedComment);
//        }

//        //DELETE
//        public ArticleComment delete (Long ArticleCommentId){
//            //대상 찾기
//            ArticleComment target = articleCommentRepository.findById(ArticleCommentId).orElse(null);
//            //잘못된 요청 처리하기
//            if (target == null) {
//                return null;
//            }
//            //대상 삭제하기
//            articleCommentRepository.delete(target);
//            return target;
//        }
}


