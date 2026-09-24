package com.LinkSphere.ConnectionService.service;

import com.LinkSphere.ConnectionService.entity.Person;
import com.LinkSphere.ConnectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    public void createPerson(Long userId,String name){
        Person person=Person.builder()
                .name(name)
                .userId(userId)
                .build();
        personRepository.save(person);
    }
}
