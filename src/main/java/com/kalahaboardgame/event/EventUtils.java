package com.kalahaboardgame.event;

/**
 * Event utility class
 */
public class EventUtils {

    private EventUtils() {
        // utils class - prevent instantiation!
    }

    /**
     * Assert if event object is a valid Event object, if yes, then flow will continue, otherwise IllegalArgumentException will be thrown.
     * This exception should never be thrown, if thrown then it indicates a programmer error.
     *
     * @param object event object
     */
    public static void assertIfUpdateContainsValidEventObject(final Object object) {
        if (!(object instanceof Event)) {
            throw new IllegalArgumentException("Update should have valid Event object");
        }
    }

}
