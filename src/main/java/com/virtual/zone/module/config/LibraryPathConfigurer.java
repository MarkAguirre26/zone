package com.virtual.zone.module.config;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LibraryPathConfigurer implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
//        System.setProperty("java.library.path", "C:\\Users\\koi\\.m2\\repository\\com\\jacob\\jacob\\1.18");
    }
}
