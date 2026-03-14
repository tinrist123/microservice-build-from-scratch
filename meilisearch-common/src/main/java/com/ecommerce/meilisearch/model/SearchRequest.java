package com.ecommerce.meilisearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String q;
    private String[] attributesToSearchOn;
    private String[] filter;
    private Integer page;
    private Integer hitsPerPage;
}
