```
			 _         _       _                                  _ _ _     
			| | ____ _| | __ _| |__   __ _    ___ ___  _ __ ___  | (_) |__  
			| |/ / _` | |/ _` | '_ \ / _` |  / __/ _ \| '__/ _ \ | | | '_ \ 
			|   < (_| | | (_| | | | | (_| | | (_| (_) | | |  __/ | | | |_) |
			|_|\_\__,_|_|\__,_|_| |_|\__,_|  \___\___/|_|  \___| |_|_|_.__/ 

     -------------------------------------------------------------------------
  	 |              Pit12   Pit11   Pit10   Pit9    Pit8    Pit7             |
     |                                                                       |
  	 | KalahaPit2                                                 KalahaPit1 |
     |                                                                       |
     |              Pit1    Pit2    Pit3    Pit4    Pit5    Pit6             |
  	 -------------------------------------------------------------------------

```

This repository defines components needed for Kalaha board game and its game rules. It is an Event-Driven library where every action results in an event.

The benefits about this event-driven approach are:

  - Only Kalaha board knows about pits location and its configuration (in term of who should observe and listen for a given event)
  - Pit in other hand, only knows what events it should emit and how to handle notifications, like move notification, capture seeds, etc.
  - Since all actions result in events, we could re-create the whole game situation from the emitted events.
  - This approach makes it easy to provide UI and UI components only need to register them self for the events and react on those notifications.
  
I tried my best to make sure that source code is well-documented and provide some Kalaha board sketches to make it easy to understand. Acceptance tests should cover the game rules and play scenarios.

Please look at my other repository for building Web UI that uses this library.
