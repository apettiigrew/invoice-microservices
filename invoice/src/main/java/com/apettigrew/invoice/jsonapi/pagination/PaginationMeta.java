package com.apettigrew.invoice.jsonapi.pagination;


import com.apettigrew.invoice.jsonapi.ResponseMeta;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PaginationMeta<T> implements ResponseMeta {
    private final String current_page;
    private final String from;
    private final String last_page;
    private final PaginationLinks<?> links;
    private final String per_page;
    private final String to;
    private final String total;

    public PaginationMeta(final Page<T> page) {
        this.current_page = String.valueOf(page.getNumber());
        this.from = String.valueOf(page.getNumber() * page.getSize());
        this.last_page = String.valueOf(page.getTotalPages() - 1);
        this.links = new PaginationLinks<>(page);
        this.per_page = String.valueOf(page.getSize());
        this.to = String.valueOf(page.getNumber() * page.getSize() + page.getNumberOfElements());
        this.total = String.valueOf(page.getTotalElements());
    }
}
