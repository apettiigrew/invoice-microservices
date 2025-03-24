package com.apettigrew.notification.config;

import com.sendgrid.helpers.mail.objects.Personalization;

import java.util.HashMap;
import java.util.Map;

public class DynamicTemplatePersonalization extends Personalization {

    private final Map<String, Object> dynamicTemplateData = new HashMap<>();

    public void add(String key, String value) {
        dynamicTemplateData.put(key, value);
    }

    @Override
    public Map<String, Object> getDynamicTemplateData() {
        return dynamicTemplateData;
    }

}
