package org.dows.gpt.entity;

import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(value = "call_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallMetricsEntity {
    private Long id;
    private String userId;
    private String model;
    private int promptTokens;
    private int completionTokens;
    private BigDecimal price;
    private LocalDateTime callTime;

}
