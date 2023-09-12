package com.example.ESDemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "headlines")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsHeadlines {
    @Id
    private String Id = UUID.randomUUID().toString();
    @Field(type = FieldType.Text)
    private String authors;
    @Field(type = FieldType.Keyword)
    private String category;
    @Field(type = FieldType.Text)
    private String headline;
    @Field(type = FieldType.Keyword)
    private String link;
    @Field(type = FieldType.Text, name = "short_description")
    private String shortDescription;

}

