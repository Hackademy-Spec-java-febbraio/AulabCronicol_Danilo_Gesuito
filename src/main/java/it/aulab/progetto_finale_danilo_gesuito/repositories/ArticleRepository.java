package it.aulab.progetto_finale_danilo_gesuito.repositories;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.progetto_finale_danilo_gesuito.models.Article;
import it.aulab.progetto_finale_danilo_gesuito.models.Category;
import it.aulab.progetto_finale_danilo_gesuito.models.User;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {
    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);
}
