package com.LinkSphere.ConnectionService.consumer;

import com.LinkSphere.ConnectionService.repository.PersonRepository;
import com.LinkSphere.ConnectionService.service.PersonService;
import com.LinkSphere.userService.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceConsumer {

    private final PersonRepository personRepository;
    private final PersonService personService;

    @KafkaListener(topics = "user_created_topic")
    public void handlePersonCreated(UserCreatedEvent userCreatedEvent)
    {
        log.info("Received UserCreatedEvent from user_created_topic");
        personService.createPerson(userCreatedEvent.getUserId(), userCreatedEvent.getName());
    }
}
