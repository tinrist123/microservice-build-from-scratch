package com.ecommerce.meilisearch.client;

import com.ecommerce.meilisearch.model.SearchResponse;
import com.ecommerce.meilisearch.model.SearchRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeilisearchClient {

    private static final String SEARCH_URI = "/indexes/{uid}/search";

    private final RestClient meiliSearchRestClient;

    /**
     * Performs a search using a specific target class.
     */
    public <T> SearchResponse<T> search(String indexUid, SearchRequest request, Class<T> targetType) {
        log.debug("Executing search on index: {} with query: {}", indexUid, request.getQ());

        // Cannot directly use Class<T> to deserialize the wrapper generically with
        // RestClient out-of-the-box easily
        // without ParameterizedTypeReference. So we typically prefer the
        // ParameterizedTypeReference approach
        // but we provide this for convenience if users want to map manually or if
        // Spring handles it (which it often doesn't for generic wrappers).
        // A safer way is delegating to parameter type reference.
        throw new UnsupportedOperationException(
                "Use search with ParameterizedTypeReference for generic wrapper deserialization");
    }

    /**
     * Performs a search using ParameterizedTypeReference, preserving generic types
     * during Jackson deserialization.
     */
    public <T> SearchResponse<T> search(String indexUid, SearchRequest request,
            ParameterizedTypeReference<SearchResponse<T>> typeReference) {
        log.debug("Executing search on index: {} with query: {}", indexUid, request.getQ());

        return meiliSearchRestClient.post()
                .uri(SEARCH_URI, indexUid)
                .body(request)
                .retrieve()
                .body(typeReference);
    }
}
