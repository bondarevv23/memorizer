package com.github.bondarevv23.memorizer.util;

import com.github.bondarevv23.memorizer.model.Deck;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class TestHttpRequestUtil {
    private final WebClient client;

    public <U> ResponseEntity<U> makeGet(Class<U> typeToken, String uri, Object... uriVariables) {
        ResponseEntity<U> response = client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.empty())
                .toEntity(typeToken).block();
        Objects.requireNonNull(response);
        return response;
    }

    public <U> ResponseEntity<List<U>> makeGetList(Class<U> typeToken, String uri, Object... uriVariables) {
        ResponseEntity<List<U>> response = client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.empty())
                .toEntityList(typeToken).block();
        Objects.requireNonNull(response);
        return response;
    }

    public <U, V> ResponseEntity<U> makePost(V value, Class<U> typeToken, String uri, Object... uriVariables) {
        ResponseEntity<U> response = client.post()
                .uri(uri, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(value)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.empty())
                .toEntity(typeToken).block();
        Objects.requireNonNull(response);
        return response;
    }

    public <U> ResponseEntity<U> makePost( Class<U> typeToken, String uri, Object... uriVariables) {
        ResponseEntity<U> response = client.post()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.empty())
                .toEntity(typeToken).block();
        Objects.requireNonNull(response);
        return response;
    }

    public ResponseEntity<Void> makeDelete(String uri, Object... uriVariables) {
        ResponseEntity<Void> response = client.delete()
                .uri(uri, uriVariables)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> Mono.empty())
                .toEntity(Void.class).block();
        Objects.requireNonNull(response);
        return response;
    }

    public <U> ResponseEntity<Void> makePut(U value, String uri, Object... uriVariables) {
        ResponseEntity<Void> response = client.put()
                .uri(uri, uriVariables)
                .bodyValue(value)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> Mono.empty())
                .toEntity(Void.class).block();
        Objects.requireNonNull(response);
        return response;
    }
}
