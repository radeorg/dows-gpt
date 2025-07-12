package org.dows.gpt.app.service.impl;

import org.dows.gpt.app.service.AppService;

/**
 * Application Service Implementation
 */
public class AppServiceImpl implements AppService {
    
    @Override
    public String getApplicationInfo() {
        return "GPT Application v1.0.0";
    }
} 