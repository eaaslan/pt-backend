package pt.attendancetracking.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Component
public class TimeProvider {

    private Supplier<LocalDateTime> currentTimeSupplier;

    public TimeProvider() {
        this.currentTimeSupplier = LocalDateTime::now;
    }

    public LocalDateTime getCurrentTime(){
        return currentTimeSupplier.get();
    }

    public void setCurrentTimeSupplier(LocalDateTime time){
        this.currentTimeSupplier= () -> time;
    }

    public void resetTime() {
        this.currentTimeSupplier = LocalDateTime::now;
    }

}
