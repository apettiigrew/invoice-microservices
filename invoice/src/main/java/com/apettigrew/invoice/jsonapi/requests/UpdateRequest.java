package com.apettigrew.invoice.jsonapi.requests;

import com.apettigrew.invoice.jsonapi.CreateResource;
import com.apettigrew.invoice.jsonapi.ResourceDto;
import jakarta.validation.Valid;
import lombok.Getter;

/**
 * An incoming update request. Can be used to clearly define a request as an incoming update request and to limit
 * allowed types
 * @param <T> The resource for the create request
 */
@Getter
public class UpdateRequest<T extends CreateResource<? extends ResourceDto>> {

    /**
     * The resource that this request contains
     */
    @Valid
    T data;
}
