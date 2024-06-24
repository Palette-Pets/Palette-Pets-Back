package com.palette.palettepetsback.Article.articleView.repository;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.DTO.response.PopularArticleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
    @Query("SELECT count(a) FROM Article a WHERE a.articleTags LIKE %?1%")
    Optional<Integer> countByArticleTagsContaining(String tag);

    // Main Page 인기글 List 출력
    @Query("SELECT new com.palette.palettepetsback.Article.articleView.DTO.response.PopularArticleDTO(a.articleId, a.title, " +
            " m.memberId, " +
            " m.memberNickname, m.memberImage, a.countLoves, a.countReview) " +
            "FROM Article a JOIN Member m on a.createdWho = m.memberId " +
            "WHERE a.createdAt >= :date " +
            "ORDER BY a.countLoves DESC " +
            "LIMIT 5")
    List<PopularArticleDTO> findPopularArticleByDate(LocalDateTime date);

    // fetch join = 176초
//    @Query("SELECT a FROM Article a join fetch a.member m " +
//            "where a.createdAt >= :date ORDER BY a.countLoves DESC")
//    List<Article> findPopularArticleByDate2(LocalDateTime date);
}
