package com.fitsoft.shop.sevice;

import com.fitsoft.shop.bean.Article;
import com.fitsoft.shop.bean.ArticleType;
import com.fitsoft.shop.utils.Pager;

import java.util.List;
import java.util.Map;

//业务层接口
public interface ShopService {

    List<ArticleType> getArticleTypes();

    Map<String, Object> login(String loginName, String password);

    List<ArticleType> loadArticleTypes();

    List<Article> searchArticles(String typeCode, String secondType, String title, Pager pager);

    List<ArticleType> loadSecondTypes(String typeCode);

    void deleteById(String id);

    Article getArticleById(String id);

    void updateArticle(Article article);

    void saveArticle(Article article);
}
