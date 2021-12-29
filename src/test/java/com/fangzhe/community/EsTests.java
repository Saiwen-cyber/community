package com.fangzhe.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fangzhe.community.dao.DiscussPostMapper;
import com.fangzhe.community.dao.elasticsearch.DiscussPostRepository;
import com.fangzhe.community.entity.DiscussPost;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.omg.PortableInterceptor.DISCARDING;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class EsTests {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    DiscussPostRepository discussPostRepository;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    RestHighLevelClient client;

    @Test
    public void testInsert(){

        discussPostRepository.save(discussPostMapper.selectDiscussPostByid(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostByid(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostByid(243));
        discussPostRepository.save(discussPostMapper.selectDiscussPostByid(281));


    }
    @Test
    public void testInsertList(){

        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(104,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(113,0,100));
    }

    @Test
    public void testUpdate(){
        /*覆盖*/
        DiscussPost post = discussPostMapper.selectDiscussPostByid(281);
        post.setContent("我是新人");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        discussPostRepository.deleteAll();
    }

    /**
     * 搜索
     */
    @Test
    public void testElasticSearch(){
        /*
         *构造查询条件
         */
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if(searchHits.getTotalHits() <= 0){
            return ;
        }
        //设置一个最后需要返回的实体类集合
        List<DiscussPost> posts = new ArrayList<>();
        //遍历返回的内容进行处理
        for(SearchHit<DiscussPost> searchHit:searchHits){
            //高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setTitle(highlightFields.get("title")==null
                    ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setContent(highlightFields.get("content")==null
                    ? searchHit.getContent().getContent() : highlightFields.get("content").get(0));
            //放到实体类中
            posts.add(searchHit.getContent());
        }

//        List<DiscussPost> list = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        for (DiscussPost post: posts) {
            System.out.println(JSON.toJSON(post));
        }
    }
}
