package com.kalahaboardgame.logger;

import java.util.Observable;
import java.util.Observer;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventUtils;
import org.apache.log4j.Logger;

/**
 * Replay-ability logger, to recreate situation based on events
 * <p/>
 * Created by amhamid on 7/24/15.
 */
public class ReplayAbilityLogger implements Observer {
    private final Logger logger = Logger.getLogger(ReplayAbilityLogger.class);

    public void update(final Observable observable, final Object object) {
        EventUtils.assertIfUpdateContainsValidEventObject(object);

        final Event event = (Event) object;
        logger.info("Publisher: " + observable + " - " + event);
    }

}
