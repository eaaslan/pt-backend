package pt.attendancetracking.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QrCheckInRequest {
    private String qrCode;
    private LocalDateTime clientLocalTime;
    private String clientTimeZone;

    
}