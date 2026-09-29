package com.LinkSphere.ConnectionService.event;

import lombok.Data;

@Data
public class ConnectionAcceptedEvent {
    private Long senderId;
    private Long receiverId;
}
