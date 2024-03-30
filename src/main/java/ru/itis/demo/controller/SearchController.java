package ru.itis.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.task5.VectorSearch;

@Controller
@RequiredArgsConstructor
public class SearchController {
    VectorSearch vectorSearch = new VectorSearch();

    @GetMapping("/search")
    public String getSearchResult(Model model, @RequestParam String query) {
        model.addAttribute("results", vectorSearch.searchPages(query));
        return "searchResult";
    }

    @GetMapping("/")
    public String getSearchPage(Model model) {
        return "searchPage";
    }

}
