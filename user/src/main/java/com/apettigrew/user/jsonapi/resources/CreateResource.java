package com.apettigrew.user.jsonapi.resources;

import com.apettigrew.user.jsonapi.ResourceDto;

/**
 * Handles the resource for an incoming create requests.
 * Can be used to differentiate types from create/read resources
 *
 * @param <T> The resource DTO to base the resource on
 */
public interface CreateResource<T extends ResourceDto<?>>{
    T generateDto();
}
