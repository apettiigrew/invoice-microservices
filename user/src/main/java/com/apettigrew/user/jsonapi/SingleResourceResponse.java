package com.apettigrew.user.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SingleResourceResponse <T extends Resource<? extends ResourceDto>> {

    private final T data;

    public SingleResourceResponse(final T resource){
        this.data = resource;
    }
}
