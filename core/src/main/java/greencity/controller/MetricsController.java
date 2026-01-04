package greencity.controller;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MetricsController {
    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @GetMapping(value = "/metrics", produces = "text/plain; version=0.0.4")
    public String metrics() {
        return prometheusMeterRegistry.scrape();
    }
}
