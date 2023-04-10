package ru.practicum;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("$stat.url")
    private String url;

    @Value("$timeout.size")
    private int timeout;

    @Bean
    public WebClient webClientWithTimeout() {
        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}