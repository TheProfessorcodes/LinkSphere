package com.LinkSphere.ConnectionService.controller;

import com.LinkSphere.ConnectionService.entity.Person;
import com.LinkSphere.ConnectionService.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ConnectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionController {
    private final ConnectionsService connectionsService;

    // EXISTING API — DON'T BREAK IT
    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>> getFirstDegreeConnectionsOfUser(
            @PathVariable Long userId) {

        List<Person> personList =
                connectionsService.getFirstDegreeConnectionsOfUser(userId);

        return ResponseEntity.ok(personList);
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<Void> sendConnectionRequest(@PathVariable Long userId) {
         connectionsService.sendConnectionRequest(userId);
         return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Void> acceptConnectionRequest(@PathVariable Long userId) {
        connectionsService.acceptConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long userId) {
        connectionsService.rejectConnectionRequest(userId);
        return ResponseEntity.noContent().build();
    }

   
}
