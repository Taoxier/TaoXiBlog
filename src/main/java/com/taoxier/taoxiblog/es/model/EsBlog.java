//package com.taoxier.taoxiblog.es.model;
//
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//import java.util.Date;
///**
// * @Author taoxier
// * @Date 2025/7/17 下午9:23
// * @描述
// */
//@Data
//@Document(indexName = "blogs")
//public class EsBlog {
//    @Id
//    private Long id;
//
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String title;
//
//    @Field(type = FieldType.Text, analyzer = "ik_max_word")
//    private String content;
//
//    @Field(type = FieldType.Date)
//    private Date createTime;
//
//    @Field(type = FieldType.Boolean)
//    private boolean published;
//
//    @Field(type = FieldType.Keyword)
//    private boolean password;
//
//}
//
