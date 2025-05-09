package it.aulab.progetto_finale_danilo_gesuito.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
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
import it.aulab.progetto_finale_danilo_gesuito.repositories.ArticleRepository;
import it.aulab.progetto_finale_danilo_gesuito.services.ArticleService;
import it.aulab.progetto_finale_danilo_gesuito.services.CrudService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/articles")
public class ArticleController {
    
    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto,Category,Long> categoryService;
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    // Rotta per la visualizzazione di tutti gli articoli //
    @GetMapping
    public String articleIndex(Model viewModel){
        viewModel.addAttribute("title", "Tutti gli articoli");
        
        List<ArticleDto> articles = new ArrayList<ArticleDto>();
        for(Article article: articleRepository.findByIsAcceptedTrue()){
            articles.add(modelMapper.map(article, ArticleDto.class));
        }
        
        // Ordina gli articoli per data in ordine decrescente
        articles.sort(Comparator.comparing(ArticleDto::getPublishDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        viewModel.addAttribute("articles", articles);
        
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
            return "article/create";
        }
        
        // salvataggio dell'articolo //
        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo creato con successo!");
        return "redirect:/";
    }
    
    // Rotta di dettaglio di un articolo //
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }
    
    // rotta per la modifica di un articolo
    @GetMapping("/edit/{id}")
    public String editArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article Update");
        viewModel.addAttribute("article", articleService.read(id));
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/edit";
    }
    
    // rotta di memorizzazione modifica di un articolo
    @PostMapping("/update/{id}")
    public String articleUpdate(@PathVariable("id")Long id,
                                @Valid @ModelAttribute("article") Article article,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Principal principal,
                                MultipartFile file,
                                Model viewModel) {
        
        // controllo degli errori con validazione
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Article Update");
            article.setImage(articleService.read(id).getImage());
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/edit";
        }
        
        articleService.update(id, article, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo modificato con successo!");
        return "redirect:/articles";
    }

    // rotta per la cancellazione di un articolo
    @GetMapping("/delete/{id}")
    public String articleDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        articleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo cancellato con successo!");
        
        return "redirect:/writer/dashboard";
    }
    
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model modelView) {
        modelView.addAttribute("title", "Article detail");
        modelView.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }
    
    // rotta dedicata all'azione del revisore
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {
        
        if (action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo accettato!");
        }else if (action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato!");
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Azione non valida!");
        }
        
        return "redirect:/revisor/dashboard";
    }
    
    // rotta per la ricerca di un articolo
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli trovati");
        
        List<ArticleDto> articles = articleService.search(keyword);
        
        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());
        
        viewModel.addAttribute("articles", acceptedArticles);
        
        return "article/articles";
    }
}