package com.apettigrew.user.jsonapi.requests;

import com.apettigrew.user.jsonapi.CreateResource;
import com.apettigrew.user.jsonapi.ResourceDto;
import jakarta.validation.Valid;
import lombok.Getter;

/**
 * An incoming create request. Can be used to clearly define a request as an incoming create request and to limit
 * allowed types
 * @param <T> The resource for the create request
 */
@Getter
public class CreateRequest<T extends CreateResource<? extends ResourceDto>> {

    /**
     * The resource that this request contains
     */
    @Valid
    T data;
}
