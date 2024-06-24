package com.palette.palettepetsback.Article.articleWrite.controller;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleImageDto;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleUpdateRequest;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleWriteDto;
import com.palette.palettepetsback.Article.articleWrite.repository.ArticleWriteRepository;
import com.palette.palettepetsback.Article.articleWrite.response.Response;
import com.palette.palettepetsback.Article.articleWrite.service.ArticleWriteService;
import com.palette.palettepetsback.Article.redis.ArticleWriteRedis;
import com.palette.palettepetsback.Article.redis.service.ArticleRedisService;
import com.palette.palettepetsback.config.SingleTon.BadWordService;
import com.palette.palettepetsback.config.SingleTon.ViewerLimit;
import com.palette.palettepetsback.config.exceptions.BadWordException;
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
    private final ArticleWriteRepository articleWriteRepository;
    private final ArticleRepository articleRepository;
    private final ArticleRedisService articleRedisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BadWordService badWordService;
//    @Autowired
//    public ArticleWriteController(ArticleWriteService articleWriteService, ArticleWriteRepository articleWriteRepository) {
//        this.articleWriteService = articleWriteService;
//        this.articleWriteRepository = articleWriteRepository;
//    } // 이거 대신에 @RequiredArgsConstructor 이거를 붙이면 된다

    //GET
//    @GetMapping("/Get/article")
//    public List<Article> index(){
//        log.info("index");
//        return articleWriteService.index();
//    }
//
    //게시글 목록 조회
//    @GetMapping("/article/all")
//    @ResponseStatus(HttpStatus.OK)
//    public Response findAllArticles(@RequestParam(defaultValue = "0") final Integer page) {
//        return Response.success(articleWriteService.findAllArticles(page));
//    }


    private final ViewerLimit viewerLimit;


    //게시글 단건 조회
    @GetMapping("/articles/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response findArticle(@PathVariable final Long articleId,
                                HttpServletRequest request) {
        //조회수 증가 처리율 제한 추가

        String sessionId = request.getSession().getId();
        String key = "session_id_" + sessionId;
        if (!redisTemplate.hasKey(key)) {
            System.out.println("=======조회수 상승================");
            articleWriteService.updateCountViews(articleId);
            redisTemplate.opsForValue().set(key, sessionId, 600, TimeUnit.SECONDS);
        } else {
            System.out.println("=======이미 조회수를 올린 사람입니다.================");
        }
        ////단건 응답

        if (viewerLimit.viewLimit(request)) {
            articleWriteService.updateCountViews(articleId);
        }

        //단건 응답

        return Response.success(articleWriteService.findArticle(articleId));
    }


    //게시글 등록 --- 완료
    @PostMapping(path = "/Post/article")
    public ResponseEntity<String> create(@Valid @RequestPart("dto") ArticleWriteDto dto,
                                         @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("dto = {}", dto);
        log.info("files = {}", files);
        AuthInfoDto memberInfo = JWTUtil.getMemberInfo(); //토큰을 가져와서 멤버아이디 찾아내기
//        log.info(String.valueOf(memberInfo.getMemberId()));
        if (memberInfo == null) {
            return null;
        }
        dto.setCreatedWho(memberInfo.getMemberId());

        try {
            badWordService.filterKomoran(dto.getTitle() + "_" + dto.getContent());
        } catch (BadWordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        dto.setContent(
                dto.getContent()
                        .replaceAll(" ", "&nbsp;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("\n", "<br>")
        );

        //글 정보 DB 등록 -> article table
        Article created = articleWriteService.create(dto);

        //Redis에 글 도배 방지 - 1분 등록
        articleRedisService.saveArticleWrite(memberInfo.getMemberId());


        //object storage upload
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                String fileName = articleWriteService.uploadArticleImage("article/img", file);
                //글 이미지 정보 DB 등록 -> img_article table
                ArticleImageDto imageDto = new ArticleImageDto();
                imageDto.setArticleId(created.getArticleId());
                imageDto.setImgUrl(fileName);
                articleWriteService.createImgArticle(imageDto);
            }
        }

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body("글 작성이 완료되었습니다.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    //게시글 이미지 등록
    @PostMapping("/Post/img")
    public boolean createArticleImg(@RequestBody ArticleImageDto dto) {
        return articleWriteService.createImgArticle(dto) != null;
    }

    //게시글 수정 단건 조회
    @GetMapping("/article/getUpdateArticle/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response getUpdateArticle(@PathVariable final Long articleId
            , @JwtAuth final AuthInfoDto authInfoDto) {

        //단건 응답
        return Response.success(articleWriteService.getUpdateArticle(articleId));
    }

    //게시글 수정 -> 변경
    @PutMapping("/articles/update/{articleId}")
    @ResponseStatus(HttpStatus.OK)
    public Response editArticle(@PathVariable final Long articleId,
                                @Valid @RequestPart(value = "dto") final ArticleUpdateRequest req,
                                @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                @JwtAuth final AuthInfoDto authInfoDto) {

        return Response.success(articleWriteService.editArticle(articleId, req, authInfoDto, files));
    }


    //업데이트 할때는 Article.state는 modified(수정됨)article_id,title ,content,created_at 4개가 들어가서 수정
    //게시글 수정
//    @PatchMapping("/Patch/{id}")
//    public ResponseEntity<Article> update( @PathVariable Long id,
//                                                @Valid
//                                              @RequestBody ArticleWriteDto dto){
//        Article updated = articleWriteService.update(id,dto); // 서비스를 통해 게시글 수정
//        return (updated != null)?//수정되면 정상, 안되면 오류 응답
//                ResponseEntity.status(HttpStatus.OK).body(updated):
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//    }


    // 삭제할때는 Article.state는 deleted (삭제됨) article.is_deleted는 1로 수정 article_id 만 있으면 됨
    //DELETE -- 완료
    @DeleteMapping("/Delete/{articleId}")
    public ResponseEntity<String> delete(@PathVariable Long articleId) {

        articleWriteService.delete(articleId);

        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");

    }


    //게시글 삭제 ->
//    @DeleteMapping("/article/{articleId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Response deleteArticle(@PathVariable final Long articleId,
//                                  @JwtAuth final AuthInfoDto authInfoDto){
//        articleWriteService.deleteArticle(articleId,authInfoDto);
//        return Response.success();
//    }

    //게시글 이미지 삭제
//    @DeleteMapping("{id}/img")
//    public boolean deleteArticleImg(@PathVariable ("id")Long id,@RequestBody List<Long>imgIds ){
//        articleWriteService.deleteImgArticle(imgIds);
//        return true;
//    }


}


