package org.hospital;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BackendEventListener {

    @EventListener
    public void onReady(ApplicationReadyEvent readyEvent) {
        log.info("Application Ready");


    }


    @EventListener
    public void onClosed(ContextClosedEvent closedEvent) {
        log.info("Application Closed");
    }
}
