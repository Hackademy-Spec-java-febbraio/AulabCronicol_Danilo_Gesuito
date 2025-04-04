package it.aulab.progetto_finale_danilo_gesuito.repositories;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.progetto_finale_danilo_gesuito.models.Category;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {
    
}
