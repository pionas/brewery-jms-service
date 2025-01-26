package pl.excellentapp.brewery.beer.jms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.excellentapp.brewery.beer.jms.config.JmsConfig;
import pl.excellentapp.brewery.beer.jms.model.HelloWorldMessage;
import pl.excellentapp.brewery.beer.jms.utils.ModelIdProvider;

@RequiredArgsConstructor
@Component
@Slf4j
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ModelIdProvider modelIdProvider;
    private final ObjectMapper objectMapper;

//    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        log.info("I'm Sending a message");
        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(modelIdProvider.random())
                .message("Hello World!")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
        log.info("Message Sent!");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        final var message = HelloWorldMessage
                .builder()
                .id(modelIdProvider.random())
                .message("Hello")
                .build();
        final var receviedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                try {
                    final var helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "pl.excellentapp.brewery.beer.jms.model.HelloWorldMessage");
                    log.info("Sending Hello");
                    return helloMessage;

                } catch (JsonProcessingException e) {
                    throw new JMSException("boom");
                }
            }
        });

        log.info("Receive Message: {}", receviedMsg.getBody(String.class));

    }

}