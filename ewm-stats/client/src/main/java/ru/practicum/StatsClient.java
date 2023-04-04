package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsClient {
    private final WebClient webClient;



}
