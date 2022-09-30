package com.sangeng.runner;

import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.Article;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
/**
 * 在项目初始化后，执行的预处理工作（将数据库中的浏览量数据读取到redis中）
 */
public class ViewCountRunner implements CommandLineRunner {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;
    @Override
    public void run(String... args) throws Exception {
        //从数据库中查询出所有文章信息
        List<Article> articleList = articleMapper.selectList(null);

        //封装至Map集合，最终以map集合以value的形式存储至redis数据库中
        //map集合中key为String类型的（因为我们封装的redisCache操作的是String类型的key），value是integer类型的，使用redis对Integer类型增加更方便
        Map<String, Integer> viewCountMap = articleList.stream().collect(
                Collectors.toMap(article -> article.getId().toString(), article -> article.getViewCount().intValue()));
        //使用自己封装的redisCache中的方法将浏览量map存储至redis中，实现对于浏览量的预处理工作
        redisCache.setCacheMap(SystemConstants.REDIS_VIEW_COUNT_KEY,viewCountMap);
    }
}
