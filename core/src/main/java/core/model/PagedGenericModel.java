package core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonSerialize
@NoArgsConstructor
public class PagedGenericModel<T> {

    List<T> content;

    @JsonProperty("total_pages")
    long totalPages;

    @JsonProperty("total_elements")
    long totalElements;

    int page;

    int size;

    public static <T> PagedGenericModel of(List<T> content, long totalPages, long totalElements, int page, int size) {
        PagedGenericModel<T> pagedProfileResponse = new PagedGenericModel();
        pagedProfileResponse.content = content;
        pagedProfileResponse.totalPages = totalPages;
        pagedProfileResponse.totalElements = totalElements;
        pagedProfileResponse.page = page;
        pagedProfileResponse.size = size;

        return pagedProfileResponse;
    }
}