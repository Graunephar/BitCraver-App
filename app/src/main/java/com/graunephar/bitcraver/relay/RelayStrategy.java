package com.graunephar.bitcraver.relay;

/**
 * A relay and all of its functionality
 * Started from yocto Relay so the methods are very closely related to the functionality of that relay
 * The classes that imlpement this interface are the only ones allowed to talk to the hardware releays.
 * So if you need it in a relay extend this interface! Also the only classes allowed to talk to the strategies
 * are the ones which extend relay state! So you should probably also take a look at that interface and
 * corresponding class implementation.
 */
public interface RelayStrategy {

    void requestInit();

    void activateRelayShort(); // Actually activate the relay component

    boolean isRelayResponding();

    void activateRelayUntilToldToSwitchBack();

    void deactivateRelay();
}
