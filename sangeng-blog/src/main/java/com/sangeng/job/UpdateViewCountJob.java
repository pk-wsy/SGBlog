package com.sangeng.job;

import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    /**
     * 使用Spring提供的定时任务
     * 因为已在主启动类中开启，因此会扫描到这个注解，按照cron表达式执行下面的定时任务方法
     * 这里的定时任务是每隔5s执行一次
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateViewCount(){
        //根据redis中浏览量的key找到对应存储浏览量的map集合
        Map<String, Integer> viewCountMap = redisCache.getCacheMap(SystemConstants.REDIS_VIEW_COUNT_KEY);

        //由于map集合直接存储到数据库中不好存储，考虑将map中多个entry转换为多个对象，放入到对象集合中
        List<Article> viewCountList = viewCountMap.entrySet().stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                .collect(Collectors.toList());

        //对于List集合，可以实现批处理存入到数据库中
        articleService.updateBatchById(viewCountList);
    }
}
