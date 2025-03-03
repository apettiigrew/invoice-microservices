package com.apettigrew.invoice.jsonapi;

import com.apettigrew.invoice.jsonapi.pagination.PaginationMeta;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultipleResourceResponse<T extends Resource<? extends ResourceDto>> {
    private final List<T> data;
    private ResponseMeta meta;
    private ResponseLinks links;

    public MultipleResourceResponse(final List<T> resource){
        this.data = resource;
    }

    public MultipleResourceResponse(final Page<T> resources){
        this.data = resources.getContent();
        final var paginationMeta = new PaginationMeta<>(resources);
        this.links = paginationMeta.getLinks();
        this.meta = paginationMeta;
    }
}
