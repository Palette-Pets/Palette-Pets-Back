package com.palette.palettepetsback.Article.articleView.controller;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.DTO.response.ArticleResponseDTO;
import com.palette.palettepetsback.Article.articleView.DTO.PageableDTO;
import com.palette.palettepetsback.Article.articleView.service.ArticleKomoranService;
import com.palette.palettepetsback.Article.articleView.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    //@Autowired
    private final ArticleService articleService;
    private final ArticleKomoranService articleKomoranService;

    private final Integer PAGE_SIZE;

    @GetMapping("/")
    public ResponseEntity<?> getList(@ModelAttribute PageableDTO request) {
        int startPage = (request.getPage()-1) * PAGE_SIZE;
        Sort sort = Sort.by(Sort.Direction.ASC, request.getSort());
        Pageable pageable = PageRequest.of(startPage,startPage + 10, sort);

        Page<Article> articles = articleService.getList(pageable);

        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/test")
    public ResponseEntity<List<Article>> getTest() {
        List<Article> articles = articleService.getTest();

        return ResponseEntity.ok().body(articles);
    }

//    //리스트 출력하기(페이징, 정렬)
//    @GetMapping("/list")
//    public ResponseEntity<List<ArticleResponseDTO>> tagSearch(@ModelAttribute PageableDTO pd) {
//        log.info("pd: {}", pd);
//        List<ArticleResponseDTO> articles = articleService.searchList(pd);
//        log.info("articles: {}", articles);
//        return ResponseEntity.ok().body(articles);
//    }

    @GetMapping("/list")
    public ResponseEntity<List<ArticleResponseDTO>> tagSearch(@ModelAttribute PageableDTO pd) {
        log.info("pd: {}", pd);
        List<ArticleResponseDTO> articles = articleService.searchList(pd);
        log.info("articles: {}", articles);
        return ResponseEntity.ok().body(articles);
    }





    //검색 & 리스트 출력(페이징, 정렬, 문장 분리)
    @GetMapping("/listLabel")
    public ResponseEntity<List<ArticleResponseDTO>> labelSearch(@ModelAttribute PageableDTO pd) {
        //Article article = new Article();
        List<ArticleResponseDTO> articles = articleKomoranService.searchLabelList(pd);
        return ResponseEntity.ok().body(articles);

    }
    //태그만 출력
    @GetMapping("/listTest")
    public ResponseEntity<List<ArticleResponseDTO>> labelSearch(@RequestParam String articleTags) {
        //Article article = new Article();
        List<ArticleResponseDTO> articles = articleService.searchTest(articleTags);

        System.out.println(articles);
        return ResponseEntity.ok().body(articles);
    }
    @GetMapping("/listCount")
    public ResponseEntity<Integer> count(@RequestParam String where) {
        return ResponseEntity.ok().body(articleService.count(where));
    }

}
