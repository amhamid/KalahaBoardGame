package com.ammar.kalahacorelibrary.pubsub.audit;

import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.pubsub.Observable;
import com.ammar.kalahacorelibrary.pubsub.ObservableBase;
import com.ammar.kalahacorelibrary.pubsub.Observer;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Replay-able logger, to recreate situation based on events
 * <p>
 * Created by amhamid on 7/24/15.
 */
public class ReplayableEventPublisher extends ObservableBase implements Observer {
    private final Logger logger = Logger.getLogger(ReplayableEventPublisher.class);

    public ReplayableEventPublisher() {
        this.observerMap = new LinkedHashMap<>();
    }

    public void update(final Observable observable, final Event event) {
        logger.info("Publisher: " + observable + " - " + event);
        publishEvent(event);
    }

    @Override
    public void addObserver(final Set<EventType> eventTypes, final Observer observer) {
        eventTypes.stream().forEach(eventType -> addObserver(eventType, observer));
    }

    private void publishEvent(final Event event) {
        notifyObservers(event);
    }

}
