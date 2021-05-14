package de.vantrex.simplenetty.listener;

import de.vantrex.simplenetty.session.Session;

public interface SimpleSessionListener {

    void onDisconnect(Session session);

    void onConnect(Session session);

}
