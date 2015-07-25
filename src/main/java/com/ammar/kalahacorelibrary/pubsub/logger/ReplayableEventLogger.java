package com.ammar.kalahacorelibrary.pubsub.logger;

import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.pubsub.Observable;
import com.ammar.kalahacorelibrary.pubsub.Observer;
import org.apache.log4j.Logger;

/**
 * Replay-able logger, to recreate situation based on events
 * <p/>
 * Created by amhamid on 7/24/15.
 */
public class ReplayableEventLogger implements Observer {
    private final Logger logger = Logger.getLogger(ReplayableEventLogger.class);

    public void update(final Observable observable, final Event event) {
        logger.info("Publisher: " + observable + " - " + event);
    }

}
