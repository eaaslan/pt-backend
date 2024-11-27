package pt.attendancetracking.controller;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.attendancetracking.model.QrCheckInRequest;
import pt.attendancetracking.service.AppointmentService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrScanController {

    private static final String GYM_QR_CODE = "GYM_LOCATION_001";
    private final AppointmentService appointmentService;

    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrCode() {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            var bitMatrix = qrCodeWriter.encode(
                    GYM_QR_CODE,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(outputStream.toByteArray());

        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().build();

        }
    }

    @PostMapping("check-in/{memberId}")
    public ResponseEntity<?> handleQrCheckIn(
            @PathVariable Long memberId,
            @RequestBody QrCheckInRequest request
    ) {

        if (!GYM_QR_CODE.equals(request.getQrCode())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid qr code"
            ));
        }
        try {
            return appointmentService.checkIn(memberId, request.getClientLocalTime())
                    .map(appointment -> ResponseEntity.ok(Map.of(
                            "message", "Check in successful",
                            "appointment", appointment,
                            "checkInTime", appointment.getCheckInTime()
                    ))).orElse(ResponseEntity.badRequest().body(Map.of(

                            "error", "check in failed"
                    )));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()


            ));
        }


    }


}
