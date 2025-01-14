package io.wangk.peekaboo.common.flow.timer;

import cn.hutool.core.collection.CollUtil;
import io.wangk.peekaboo.common.flow.model.FlowTaskTimeoutJob;
import io.wangk.peekaboo.common.flow.service.FlowTaskTimeoutJobService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 流程任务超时处理定时器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EnableScheduling
@Component
@Slf4j
public class FlowTaskTimeoutTimer {

    @Autowired
    private FlowTaskTimeoutJobService flowTaskTimeoutJobService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private TaskService taskService;

    private RLock distLock;

    @PostConstruct
    public void init() {
        distLock = redissonClient.getLock("FLOW_TASK_TIMEOUT_TIMEER");
    }

    /**
     * 每五分钟执行一次。
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void execute() {
        if (!distLock.tryLock()) {
            return;
        }
        try {
            List<FlowTaskTimeoutJob> jobList = flowTaskTimeoutJobService.getExecutableList();
            if (CollUtil.isEmpty(jobList)) {
                return;
            }
            for (FlowTaskTimeoutJob job : jobList) {
                TaskQuery query = taskService.createTaskQuery().active().taskId(job.getTaskId());
                Task task = query.singleResult();
                if (task != null) {
                    flowTaskTimeoutJobService.executeJob(job, task);
                }
            }
        } catch (Exception e) {
            log.error("Failed to call FlowTaskTimeoutTimer.execute", e);
        } finally {
            distLock.unlock();
        }
    }
}
