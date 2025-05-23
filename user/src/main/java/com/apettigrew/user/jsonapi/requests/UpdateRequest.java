package com.apettigrew.user.jsonapi.requests;

import com.apettigrew.user.jsonapi.ResourceDto;
import com.apettigrew.user.jsonapi.resources.UpdateResource;
import jakarta.validation.Valid;
import lombok.Getter;

/**
 * An incoming update request. Can be used to clearly define a request as an incoming update request and to limit
 * allowed types
 * @param <T> The resource for the create request
 */
@Getter
public class UpdateRequest<T extends UpdateResource<? extends ResourceDto>> {
    /**
     * The resource that this request contains
     */
    @Valid
    T data;
}
