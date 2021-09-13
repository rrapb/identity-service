package com.ubt.identityservice.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNotification {

    private String sender;
    private String receiver;
    private String subject;
    private Object content;
    private String priority;
}
