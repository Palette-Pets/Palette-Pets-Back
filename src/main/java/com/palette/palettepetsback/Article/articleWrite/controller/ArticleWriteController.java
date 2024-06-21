package com.palette.palettepetsback.Article.articleWrite.controller;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleImageDto;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleUpdateRequest;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleWriteDto;


import com.palette.palettepetsback.Article.articleWrite.response.Response;
import com.palette.palettepetsback.Article.articleWrite.service.ArticleWriteService;
import com.palette.palettepetsback.config.SingleTon.BadWordService;
import com.palette.palettepetsback.config.exceptions.BadWordException;
import com.palette.palettepetsback.Article.redis.service.ArticleRedisService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@CrossOrigin //리액트에서 넘어올때 포트가 다르면 오류가 생기는걸 해결해줌
@Log4j2
@RequiredArgsConstructor //final이 붙은 필드들을 자동 생성해주는 생성자 -> Autowired안써도 되게 해주는거
public class ArticleWriteController {

    private final ArticleWriteService articleWriteService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BadWordService badWordService;
    private final ArticleRedisService articleRedisService;


    //게시글 단건 조회
    @GetMapping("/articles/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findArticle(@PathVariable final Long articleId,
                                HttpServletRequest request){
        //조회수 증가 처리율 제한 추가
        String sessionId = request.getSession().getId();
        String key = "session_id_"+sessionId;
        if(!redisTemplate.hasKey(key)) {
            System.out.println("=======조회수 상승================");
            articleWriteService.updateCountViews(articleId);
            redisTemplate.opsForValue().set(key, sessionId, 600, TimeUnit.SECONDS);
        }
        else{
            System.out.println("=======이미 조회수를 올린 사람입니다.================");
        }
        ////단건 응답
        return Response.success(articleWriteService.findArticle(articleId));
    }



    //게시글 등록 --- 완료
    @PostMapping(path="/Post/article")
    public ResponseEntity<String> create(@Valid @RequestPart("dto") ArticleWriteDto dto,
                                          @RequestPart(value="files",required = false) List<MultipartFile> files){
        log.info("dto = {}", dto);
        log.info("files = {}", files);
        AuthInfoDto memberInfo = JWTUtil.getMemberInfo(); //토큰을 가져와서 멤버아이디 찾아내기
//        log.info(String.valueOf(memberInfo.getMemberId()));
        if(memberInfo == null) {
            return null;
        }
        dto.setCreatedWho(memberInfo.getMemberId());

        try {
            badWordService.filterKomoran(dto.getTitle()+"_"+dto.getContent());
        } catch (BadWordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }


        //글 정보 DB 등록 -> article table
        Article created = articleWriteService.create(dto);

        //Redis에 글 도배 방지 - 1분 등록
        articleRedisService.saveArticleWrite(memberInfo.getMemberId());


        //object storage upload
            if(files != null && !files.isEmpty()){
                for (MultipartFile file : files) {

                    String fileName = articleWriteService.uploadArticleImage("article/img", file);
                    //글 이미지 정보 DB 등록 -> img_article table
                    ArticleImageDto imageDto = new ArticleImageDto();
                    imageDto.setArticleId(created.getArticleId());
                    imageDto.setImgUrl(fileName);
                    articleWriteService.createImgArticle(imageDto);
                }
            }

        return (created != null)?
                ResponseEntity.status(HttpStatus.OK).body("글 작성이 완료되었습니다."):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build() ;
    }


    //게시글 이미지 등록
    @PostMapping("/Post/img")
    public boolean createArticleImg(@RequestBody ArticleImageDto dto){
        return articleWriteService.createImgArticle(dto)!=null;
    }

    //게시글 수정 단건 조회
    @GetMapping("/article/getUpdateArticle/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response getUpdateArticle(@PathVariable final Long articleId
                                     ,@JwtAuth final AuthInfoDto authInfoDto){

        //단건 응답
        return Response.success(articleWriteService.getUpdateArticle(articleId));
    }

    //게시글 수정 -> 변경
    @PutMapping("/articles/update/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response editArticle(@PathVariable final Long articleId,
                                @Valid @RequestPart(value = "dto") final ArticleUpdateRequest req,
                                @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                @JwtAuth final AuthInfoDto authInfoDto){

        return Response.success(articleWriteService.editArticle(articleId,req,authInfoDto,files));
    }


    // 삭제할때는 Article.state는 deleted (삭제됨) article.is_deleted는 1로 수정 article_id 만 있으면 됨
    //DELETE -- 완료
    @DeleteMapping("/Delete/{articleId}")
    public ResponseEntity<String> delete(@PathVariable Long articleId){

        articleWriteService.delete(articleId);

        return  ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");

    }
}


