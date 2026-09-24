package com.LinkSphere.postService.client;

import com.LinkSphere.postService.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ConnectionService", path = "/core")
public interface ConnectionsServiceClient {

    @GetMapping("/{userId}/first-degree")
    List<PersonDto> getFirstDegreeConnectionsOfUser(
            @PathVariable("userId") Long userId
    );
}