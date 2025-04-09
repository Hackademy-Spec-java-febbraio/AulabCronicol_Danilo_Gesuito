package it.aulab.progetto_finale_danilo_gesuito.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_danilo_gesuito.dtos.ArticleDto;
import it.aulab.progetto_finale_danilo_gesuito.dtos.CategoryDto;
import it.aulab.progetto_finale_danilo_gesuito.models.Article;
import it.aulab.progetto_finale_danilo_gesuito.models.Category;
import it.aulab.progetto_finale_danilo_gesuito.services.ArticleService;
import it.aulab.progetto_finale_danilo_gesuito.services.CrudService;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/article")
public class ArticleController {
    
    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto,Category,Long> categoryService;

    @Autowired
    private ArticleService articleService;

    // Rotta per la visualizzazione di tutti gli articoli //
    @GetMapping
    public String articleIndex(Model viewModel){
        viewModel.addAttribute("title", "Tutti gli articoli");

        List<ArticleDto> articles = articleService.readAll();

        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        viewModel.addAttribute("article", articles);

        return "article/articles";
    }

    // Rotta per la creazione di un articolo //
    @GetMapping("/create")
    public String articleCreate(Model viewModel){
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }
    
    // Rotta per lo store di un articolo //
    @PostMapping
    public String articleStore(@Valid @ModelAttribute("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                MultipartFile file,
                                Model viewModel){ 
        
        // controllo degli errori //
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "articles/create";
        }
        
        // salvataggio dell'articolo //
        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo creato con successo!");
        return "redirect:/";
    }

    // Rotta per la visualizzazione di un singolo articolo //
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model modelView) {
        modelView.addAttribute("article", "Article detail");
        modelView.addAttribute("article", articleService.read(id));
        return "article/detail";
    }
}
