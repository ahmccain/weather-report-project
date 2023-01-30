package model.output;

import model.object.Email;

public class OfflineEmail implements EmailStrategy {
    @Override
    public boolean sendReport(Email email) {
        System.out.println("""
                Current weather for Raleigh
                Temperature is now 24.19°C
                Wind speed is 6.17 m/s
                Wind direction is 50.0°
                75.0 cloud(s)
                Precipitation in mm/hr: 0.0
                Air quality index is 45.0""");
        return true;
    }
}
