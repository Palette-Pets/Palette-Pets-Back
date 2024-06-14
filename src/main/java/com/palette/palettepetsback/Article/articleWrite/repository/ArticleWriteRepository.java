package com.palette.palettepetsback.Article.articleWrite.repository;

import com.palette.palettepetsback.Article.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleWriteRepository extends JpaRepository<Article,Long> {
//    Page<Article> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE Article e SET e.state = 'DELETED', e.isDeleted = true  WHERE e.articleId = :id")
    public void deleteArticleByArticleId(@Param("id") Long id);
    @Modifying
    @Query("UPDATE Article e SET e.countViews = :newValue WHERE e.articleId = :id")
    public void updateCountViews(@Param("id") Long id, @Param("newValue") int newValue);

    @Modifying
    @Query("UPDATE Article e SET e.countReview = :newValue WHERE e.articleId = :id")
    public void updateCountReviews(@Param("id") Long id, @Param("newValue") int newValue);

    @Modifying
    @Query("UPDATE Article e SET e.countReport = :newValue where e.articleId = :id")
    public void incrementReportCount(@Param("id")Long id,@Param("newValue") int newValue);
}
