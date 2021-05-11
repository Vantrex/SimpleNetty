package de.vantrex.simplenetty.server.settings;

import de.vantrex.simplenetty.protocol.Protocol;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class SimpleServerSettings {

    private int port;
    private int bossThreads;
    private int workerThreads;
    private Protocol<?> protocol;
    private boolean logger;

}
