package com.apettigrew.invoice.jsonapi.pagination;


import com.apettigrew.invoice.jsonapi.ResponseLinks;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Getter
public class PaginationLinks<T> implements ResponseLinks {
    private final String first;
    private final String last;
    private final String prev;
    private final String next;

    public PaginationLinks(final Page<T> page) {
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();

        this.first = baseUrl + "?page[number]=0&page[size]=" + page.getSize();
        this.last = baseUrl + "?page[number]=" + (page.getTotalPages() - 1) + "&page[size]=" + page.getSize();
        this.prev = page.hasPrevious() ? baseUrl + "?page[number]=" + (page.getNumber() - 1) + "&page[size]=" + page.getSize() : null;
        this.next = page.hasNext() ? baseUrl + "?page[number]=" + (page.getNumber() + 1) + "&page[size]=" + page.getSize() : null;
    }
}
