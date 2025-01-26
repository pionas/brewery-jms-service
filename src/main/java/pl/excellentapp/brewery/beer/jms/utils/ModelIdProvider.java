package pl.excellentapp.brewery.beer.jms.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModelIdProvider {

    public UUID random() {
        return UUID.randomUUID();
    }
}
