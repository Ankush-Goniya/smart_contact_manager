package com.SCM.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    // actual text content
    private String content;

    // message type (enum)
    @Builder.Default
    private MessageType type = MessageType.blue;
}
