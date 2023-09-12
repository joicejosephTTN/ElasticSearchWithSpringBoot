package com.example.ESDemo.controller;

import com.example.ESDemo.entity.NewsHeadlines;
import com.example.ESDemo.service.NewsHeadlinesService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "headlines")
public class NewsHeadlinesController {

    @Autowired
    private NewsHeadlinesService newsHeadlinesService;

    @GetMapping(value = "/all")
    public Iterable<NewsHeadlines> getAllHeadlines(){
        return newsHeadlinesService.getAllHeadlines();
    }


    @PostMapping(value = "/insert")
    public NewsHeadlines insertHeadline(@RequestBody NewsHeadlines newsHeadlines){
        return newsHeadlinesService.insertHeadLine(newsHeadlines);
    }

    @SneakyThrows
    @GetMapping(value = "/dynamicQuery")
    public List<NewsHeadlines> dynamicSearchHeadlines(){
        return newsHeadlinesService.dynamicQuery();
    }
}
