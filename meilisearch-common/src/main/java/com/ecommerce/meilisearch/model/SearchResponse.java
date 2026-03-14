package com.ecommerce.meilisearch.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse<T> {
    private List<T> hits;
    private Integer offset;
    private Integer limit;
    private Integer estimatedTotalHits;
    private Integer totalHits;
    private Integer totalPages;
    private Integer hitsPerPage;
    private Integer page;
}
