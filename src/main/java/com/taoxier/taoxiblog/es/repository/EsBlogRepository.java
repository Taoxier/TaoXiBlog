//package com.taoxier.taoxiblog.es.repository;
//
///**
// * @Author taoxier
// * @Date 2025/7/17 下午10:08
// * @描述
// */
//
//
//import com.taoxier.taoxiblog.es.model.EsBlog;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//public interface EsBlogRepository extends ElasticsearchRepository<EsBlog, Long> {
//    // 根据标题搜索博客
//    Page<EsBlog> findByTitleContaining(String title, Pageable pageable);
//
//    // 根据内容搜索博客
//    Page<EsBlog> findByContentContaining(String content, Pageable pageable);
//
//    // 根据标题或内容搜索博客
//    Page<EsBlog> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
//
//    // 使用 @Query 注解定义更复杂的查询
//    @Query("{\"bool\": {\"must\": [{\"match\": {\"title\": \"?0\"}}], \"filter\": {\"term\": {\"isPublished\": true}}}}")
//    Page<EsBlog> findByTitleAndPublished(String title, Pageable pageable);
//}