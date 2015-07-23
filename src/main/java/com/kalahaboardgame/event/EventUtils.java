package com.kalahaboardgame.event;

public class EventUtils {

    private EventUtils() {
        // utils class - prevent instantiation!
    }

    /**
     * Assert if update has a valid Event object, if yes then flow will continue, otherwise IllegalArgumentException will be thrown.
     * This exception should never be thrown, if thrown then it indicates a programmer error.
     *
     * @param object update object
     */
    public static void assertIfUpdateContainsValidEventObject(final Object object) {
        // if object is not instance of Event
        if (!(object instanceof Event)) {
            throw new IllegalArgumentException("Update should have valid Event object");
        }
    }

}
