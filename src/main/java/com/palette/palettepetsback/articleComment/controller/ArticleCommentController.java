package com.palette.palettepetsback.articleComment.controller;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.service.ArticleService;
import com.palette.palettepetsback.Article.articleWrite.response.Response;
import com.palette.palettepetsback.Article.articleWrite.service.ArticleWriteService;
import com.palette.palettepetsback.articleComment.dto.request.ArticleCommentAddRequest;
import com.palette.palettepetsback.articleComment.dto.response.ArticleCommentListResponse;
import com.palette.palettepetsback.articleComment.entity.ArticleComment;
import com.palette.palettepetsback.articleComment.repository.ArticleCommentRepository;
import com.palette.palettepetsback.articleComment.service.ArticleCommentService;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static com.palette.palettepetsback.Article.QArticle.article;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin //리액트에서 넘어올때 포트가 다르면 오류가 생기는걸 해결해줌
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;
    private final ArticleService articleService;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleWriteService articleWriteService;

    //Get
    @GetMapping("/Get/comments/{articleId}")
    public ResponseEntity<List<ArticleCommentListResponse>> comments(@Valid @PathVariable Long articleId) {
        //List<ArticleCommentListResponse>
        //서비스에 위임
        Article article = articleService.findById(articleId);
        log.info(String.valueOf(articleId));

        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(articleCommentService.comments(articleId));
    }


    //POST
    @PostMapping("/Post/comments")
    public ResponseEntity<String> create(@Valid @RequestBody ArticleCommentAddRequest dto) {

        AuthInfoDto memberInfo = JWTUtil.getMemberInfo(); //토큰을 가져와서 멤버아이디 찾아내기
        if(memberInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        dto.setCreatedWho(memberInfo.getMemberId());//dto를 받아와서 멤버아이디가 없으니까  위에꺼를 가져와서 넣기
        log.info("member : " + memberInfo.getMemberId());
       ArticleComment comment = articleCommentService.create(dto);
        log.info("dto = {}",dto);

        articleWriteService.updateCountReviews(dto.getArticleId());
       return ResponseEntity.ok("작성하신 댓글이 등록되었습니다.");
    }


    //PATCH
   @PatchMapping("/Patch/comments/{articleCommentId}")
    public ResponseEntity<ArticleComment>update(@PathVariable Long articleCommentId){

        return ResponseEntity.status(HttpStatus.OK).body(new ArticleComment());
    }




    //댓글 삭제
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete (@PathVariable final Long id,
                            @JwtAuth Member member){
        articleCommentService.deleteComment(id,member);
        return Response.success();
    }



   //DELETE
//    @DeleteMapping("/Delete/comments/{articleCommentId}")
//    public ResponseEntity<Void> delete(@PathVariable Long articleCommentId){
//        //대상 찾기
//        ArticleComment target = articleCommentRepository.findById(articleCommentId).orElse(null);
//        //잘못된 요청 처리하기
//        if(target == null){ //이미 삭제된 대상인지 확인
//            return ResponseEntity.notFound().build();
//        }
//        //대상 삭제하기 대신 상태변경하기
//
//        articleCommentRepository.delete(target);//변경된 상태 저장
//        return ResponseEntity.noContent().build();
//    }

}
