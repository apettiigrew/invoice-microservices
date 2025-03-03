package com.apettigrew.invoice.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Resource <T extends ResourceDto>{
}
