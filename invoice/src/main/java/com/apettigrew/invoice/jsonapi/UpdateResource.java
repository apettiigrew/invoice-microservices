package com.apettigrew.invoice.jsonapi;


/**
 * Handles the resource for an incoming update requests.
 * Can be used to differentiate types from create/read resources
 *
 * @param <T> The resource DTDO to base the resource on
 */
public interface UpdateResource<T extends ResourceDto<?>>{
    T generateDto();
}
