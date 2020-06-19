package kub4k1.bookmanagement.adapter.incoming.api;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse {

    private String content;

    private int status;

    private LocalDateTime timestamp;

}
