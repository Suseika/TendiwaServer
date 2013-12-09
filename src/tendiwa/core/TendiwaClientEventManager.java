package tendiwa.core;

import org.tendiwa.events.*;

public interface TendiwaClientEventManager {
void event(EventMove e);

void event(EventSay eventSay);

void event(EventFovChange eventFovChange);

void event(EventInitialTerrain eventInitialTerrain);

void event(EventItemDisappear eventItemDisappear);

void event(EventGetItem eventGetItem);

void event(EventLoseItem eventLoseItem);

void event(EventItemAppear eventItemAppear);

void event(EventPutOn eventPutOn);

void event(EventWield eventWield);

void event(EventTakeOff eventTakeOff);

void event(EventUnwield eventUnwield);

void event(EventItemFly eventItemFly);
}
