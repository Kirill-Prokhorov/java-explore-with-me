package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WebClientService {
    protected final WebClient webClient;
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);

    public WebClientService(WebClient.Builder webClientBuilder, @Value("${stat-server.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void postHit(String app,
                        String uri,
                        String ip,
                        LocalDateTime timestamp) {
        EndpointHitDTO endpointHitDTO = EndpointHitDTO.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        webClient.post()
                .uri("/hit")
                .body(Mono.just(endpointHitDTO), EndpointHitDTO.class)
                .retrieve()
                .bodyToMono(EndpointHitDTO.class)
                .block();
    }

    public List<ViewStatsDTO> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .queryParam("unique", String.valueOf(unique))
                        .queryParam("uris", uris)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStatsDTO.class)
                .collectList()
                .block();
    }
}