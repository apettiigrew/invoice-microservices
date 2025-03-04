package com.apettigrew.user.jsonapi;

/**
 * Handles the resource for an incoming create requests.
 * Can be used to differentiate types from create/read resources
 *
 * @param <T> The resource DTDO to base the resource on
 */
public interface CreateResource<T extends ResourceDto<?>>{
    T generateDto();
}
