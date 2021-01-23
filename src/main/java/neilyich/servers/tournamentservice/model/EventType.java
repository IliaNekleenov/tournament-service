package neilyich.servers.tournamentservice.model;

public enum EventType {
    START_TOURNAMENT, END_TOURNAMENT, START_REG, START_REG_INTRAMURAL /*очная*/, END_REG, START_SECOND_DAY, START_PRACTICE, END_PRACTICE;

    public static EventType of(String str) {
        str = str.toLowerCase();
        boolean start = str.contains("начал");
        boolean end = str.contains("окончан") || str.contains("конец") || str.contains("конц");
        boolean reg = str.contains("регистрац");
        boolean practice = str.contains("разминк");
        boolean second = str.contains("второ");
        boolean intramural = str.contains("очн");

        if(reg) {
            if(end) {
                return END_REG;
            }
            else if(intramural) {
                return START_REG_INTRAMURAL;
            }
            else {
                return START_REG;
            }
        }

        if(practice) {
            if(end) {
                return END_PRACTICE;
            }
            else {
                return START_PRACTICE;
            }
        }

        if(start) {
            if(second) {
                return START_SECOND_DAY;
            }
            else {
                return START_TOURNAMENT;
            }
        }
        else if(end) {
            return END_TOURNAMENT;
        }
        throw new IllegalArgumentException("Could not parse event type for string: " + str);
    }
}
