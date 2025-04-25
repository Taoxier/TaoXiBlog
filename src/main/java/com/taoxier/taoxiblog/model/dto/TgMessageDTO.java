package com.taoxier.taoxiblog.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @Description ：Telegram新消息
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class TgMessageDTO {
    @JsonProperty("update_id")
    private String updateId;
    private Message message;

    @Data
    public class Message {
        @JsonProperty("message_id")
        private String messageId;
        private From from;
        private Chat chat;
        private String date;
        private String text;

        @Data
        public class From {
            private String id;
            @JsonProperty("is_bot")
            private Boolean isBot;
            @JsonProperty("first_name")
            private String firstName;
            private String username;
            @JsonProperty("language_code")
            private String languageCode;
        }

        @Data
        public class Chat {
            private String id;
            @JsonProperty("first_name")
            private String firstName;
            private String username;
            private String type;
        }
    }
}
