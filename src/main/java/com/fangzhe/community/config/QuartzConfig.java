package com.fangzhe.community.config;

import com.fangzhe.community.quartz.DeleteTmpShareImageJob;
import com.fangzhe.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * 启用spring线程池
 * @author fang
 */
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    /**配置postScoreRefreshJobDetail*/
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        //没有触发器指向时是否存储
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    /**配置postScoreRefreshTrigger*/
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshJobTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    /**配置deleteTmpShareImageJobDetail*/
    @Bean
    public JobDetailFactoryBean deleteTmpShareImageJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(DeleteTmpShareImageJob.class);
        factoryBean.setName("deleteTmpShareImageJob");
        factoryBean.setGroup("communityJobGroup");
        //没有触发器指向时是否存储
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    /**配置deleteTmpShareImageJobDetailTrigger*/
    @Bean
    public SimpleTriggerFactoryBean deleteTmpShareImageJobDetailTrigger(JobDetail deleteTmpShareImageJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(deleteTmpShareImageJobDetail);
        factoryBean.setName("deleteTmpShareImageJobTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 4);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
