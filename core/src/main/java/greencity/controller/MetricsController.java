package greencity.controller;

import greencity.dto.metric.LoginEventDto;
import greencity.metrics.ActiveUsersInMemoryMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {
    private final ActiveUsersInMemoryMetrics activeUsersInMemoryMetrics;

    @PostMapping("/recordLogin")
    public ResponseEntity<Void> recordLogin(@RequestBody LoginEventDto loginEventDto) {
        activeUsersInMemoryMetrics.recordLogin(loginEventDto.getEmail(), loginEventDto.getLoginTime());
        return ResponseEntity.ok().build();
    }
}