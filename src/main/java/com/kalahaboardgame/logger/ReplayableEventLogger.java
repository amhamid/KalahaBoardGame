package com.kalahaboardgame.logger;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.pubsub.Observable;
import com.kalahaboardgame.pubsub.Observer;
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
