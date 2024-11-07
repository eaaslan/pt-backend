package pt.attendancetracking.model;


import lombok.Data;

@Data
public class QrCheckInRequest {
    private String qrCode;
}