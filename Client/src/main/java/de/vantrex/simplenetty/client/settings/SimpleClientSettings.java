package de.vantrex.simplenetty.client.settings;

import de.vantrex.simplenetty.protocol.Protocol;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class SimpleClientSettings {

    private String address;
    private int port;
    private int threads;
    private Protocol<?> protocol;
    private boolean logger;

}
