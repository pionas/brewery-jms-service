package pl.excellentapp.brewery.beer.jms.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.excellentapp.brewery.beer.jms.config.JmsConfig;
import pl.excellentapp.brewery.beer.jms.model.HelloWorldMessage;
import pl.excellentapp.brewery.beer.jms.utils.ModelIdProvider;

@RequiredArgsConstructor
@Component
@Slf4j
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;
    private final ModelIdProvider modelIdProvider;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers, Message message) {

        log.info("I Got a Message!!!!!");
        log.info("Message headers: {}", headers);
        log.info("HelloWorldMessage: {}", helloWorldMessage);
        log.info("Message: {}", message);
    }

    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenForHello(@Payload HelloWorldMessage helloWorldMessage,
                               @Headers MessageHeaders headers,
                               Message jmsMessage,
                               org.springframework.messaging.Message springMessage) throws JMSException {

        log.info("### I Got a Message!!!!!");
        log.info("### Message headers: {}", headers);
        log.info("### HelloWorldMessage: {}", helloWorldMessage);
        log.info("### Spring Message: {}", springMessage);

        final var payloadMsg = HelloWorldMessage.builder()
                .id(modelIdProvider.random())
                .message("World!!")
                .build();
        jmsTemplate.convertAndSend(jmsMessage.getJMSReplyTo(), payloadMsg);

    }

}