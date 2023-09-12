package com.example.ESDemo.repository;
import com.example.ESDemo.entity.NewsHeadlines;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsHeadlinesRepository extends ElasticsearchRepository<NewsHeadlines,String> {

}
