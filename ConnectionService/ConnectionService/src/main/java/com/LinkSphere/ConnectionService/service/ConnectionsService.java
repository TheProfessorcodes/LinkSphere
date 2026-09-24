package com.LinkSphere.ConnectionService.service;

import com.LinkSphere.ConnectionService.auth.AuthContextHolder;
import com.LinkSphere.ConnectionService.entity.Person;
import com.LinkSphere.ConnectionService.exception.BadRequestException;
import com.LinkSphere.ConnectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsService {
    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {

        log.info("getFirstDegreeConnectionsOfUser");
        return personRepository.getFirstDegreeConnections(userId);

    }

    public void sendConnectionRequest(Long receiverId) {
        log.info("sendConnectionRequest");
        Long senderId = AuthContextHolder.getCurrentUserId();

        if(senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (alreadySentRequest) {
            throw new BadRequestException("Connection request already exists, cannot send again");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if(alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot add connection request");
        }


        personRepository.addConnectionRequest(senderId, receiverId);
        log.info("Successfully sent the connection request");
    }
    public void acceptConnectionRequest(Long senderId) {
        log.info("acceptConnectionRequest");
        Long receiverId = AuthContextHolder.getCurrentUserId();
        if(senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }
        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if(alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("Connection request already exists, cannot accept again");
        }
        personRepository.acceptConnectionRequest(senderId, receiverId);
        log.info("Successfully accepted the connection request");
    }

    public void rejectConnectionRequest(Long senderId) {
        log.info("rejectConnectionRequest");
        Long receiverId = AuthContextHolder.getCurrentUserId();
        if(senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }
        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if(alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("No Connection request exists,cannot reject it");
        }
        personRepository.rejectConnectionRequest(senderId, receiverId);
        log.info("Successfully rejected the connection request");
    }

}
