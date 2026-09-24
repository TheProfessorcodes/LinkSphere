package com.LinkSphere.ConnectionService.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionAcceptedEvent {
    private Long senderId;
    private Long receiverId;
}
