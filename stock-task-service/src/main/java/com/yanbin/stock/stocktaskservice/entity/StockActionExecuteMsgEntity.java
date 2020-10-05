package com.yanbin.stock.stocktaskservice.entity;

import com.emotibot.gemini.geminiutils.constants.task.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/27 上午8:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="stock_action_execute_msg")
@EntityListeners(AuditingEntityListener.class)
public class StockActionExecuteMsgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "appid", nullable = false)
    private String appId;

    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    // uuid
    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "action_str")
    private String actionStr;

    @Column(name = "action_context_str", nullable = false)
    private String actionContextStr;

    @Column(name = "executor_status", nullable = false)
    private TaskStatus status;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;
}
