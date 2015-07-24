package com.kalahaboardgame.pubsub;

import com.kalahaboardgame.event.Event;

/**
 * Custom Observer interface (a variant of java.util.Observer) where we can update based on Event rather than on plain Object.
 * <p/>
 * Created by amhamid on 7/25/15.
 */
public interface Observer {

    void update(Observable observable, Event event);

}
