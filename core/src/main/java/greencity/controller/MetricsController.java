package greencity.controller;

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

class LoginEventDto {
    private String email;
    private Long loginTime;

    public LoginEventDto() {
    }

    public LoginEventDto(String email, Long loginTime) {
        this.email = email;
        this.loginTime = loginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }
}