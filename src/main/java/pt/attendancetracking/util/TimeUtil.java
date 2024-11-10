package pt.attendancetracking.util;

import java.time.*;

public class TimeUtil {
    // Constants for business rules
    private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(22, 0);
    private static final Duration SLOT_DURATION = Duration.ofHours(1);


    /**
     * Checks if the given time falls within business hours
     */
    public static boolean isValidBusinessHour(LocalDateTime dateTime) {
        System.out.println("Checking business hours for: " + dateTime);
        System.out.println("In timezone: " + java.time.ZoneId.systemDefault());
        LocalTime time = dateTime.toLocalTime();
        return time.isBefore(BUSINESS_HOURS_END) &&
                time.isAfter(BUSINESS_HOURS_START);
    }

    public static LocalDateTime getCurrentTimeInUTCPlus3() {
        return ZonedDateTime.now(ZoneId.of("UTC+03:00")).toLocalDateTime();
    }

    public static LocalDateTime convertToUTC(String clientLocalTime, String clientTimeZone) {
        // Parse the local time sent by the client
        LocalDateTime localTime = LocalDateTime.parse(clientLocalTime);

        // Convert to UTC using the client's timezone
        ZonedDateTime zonedTime = localTime.atZone(ZoneId.of(clientTimeZone));
        return zonedTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }


    /**
     * Normalizes a datetime to the start of its hour slot
     */
    public static LocalDateTime normalizeToHourSlot(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Round to closest hour <= 30
     */
    public static LocalDateTime roundToNearestHour(LocalDateTime time) {
        LocalDateTime previousHour = time.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime nextHour = previousHour.plusHours(1);

        return time.getMinute() <= 30 ? previousHour : nextHour;
    }


    /**
     * Gets the start time of a slot
     */
    public static LocalDateTime getSlotStart(LocalDateTime dateTime) {
        return normalizeToHourSlot(dateTime);
    }

    /**
     * Gets the end time of a slot
     */
    public static LocalDateTime getSlotEnd(LocalDateTime dateTime) {
        return getSlotStart(dateTime).plus(SLOT_DURATION);
    }


    public static boolean isValidToCheckIn(LocalDateTime appointmentTime, LocalDateTime checkInTime) {
        LocalDateTime roundedCheckInTime = roundToNearestHour(checkInTime);
        return roundedCheckInTime.equals(appointmentTime);
    }

    /**
     * Checks if two time slots overlap
     */
    public static boolean areOverlapping(LocalDateTime slot1, LocalDateTime slot2) {
        LocalDateTime slot1Start = getSlotStart(slot1);
        LocalDateTime slot1End = getSlotEnd(slot1);
        LocalDateTime slot2Start = getSlotStart(slot2);
        LocalDateTime slot2End = getSlotEnd(slot2);

        return !(slot1End.isBefore(slot2Start) || slot2End.isBefore(slot1Start));
    }

    /**
     * Checks if a given time falls within a slot
     */
    public static boolean isWithinSlot(LocalDateTime slotTime, LocalDateTime checkTime) {
        LocalDateTime slotStart = getSlotStart(slotTime);
        LocalDateTime slotEnd = getSlotEnd(slotTime);
        LocalDateTime normalizedCheckTime = normalizeToHourSlot(checkTime);

        return normalizedCheckTime.equals(slotStart);
    }

    /**
     * Gets the time difference between slots in hours
     */
    public static long getHoursBetweenSlots(LocalDateTime slot1, LocalDateTime slot2) {
        return Duration.between(
                getSlotStart(slot1),
                getSlotStart(slot2)
        ).toHours();
    }
}