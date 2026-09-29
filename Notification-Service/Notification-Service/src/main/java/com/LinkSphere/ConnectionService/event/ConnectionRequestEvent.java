package com.LinkSphere.ConnectionService.event;

import lombok.Data;

@Data
public class ConnectionRequestEvent {
    private Long senderId;
    private Long receiverId;
}
