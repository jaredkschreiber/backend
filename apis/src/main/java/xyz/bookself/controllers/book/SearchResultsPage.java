package xyz.bookself.controllers.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultsPage {
    @JsonProperty("currentPage")
    private final int currentPage;
    @JsonProperty("totalPages")
    private final int totalPages;
    @JsonProperty("totalResultCount")
    private final long totalResultCount;
    @JsonProperty("searchResults")
    private final List<BookWithRankDTO> searchResults;
}
