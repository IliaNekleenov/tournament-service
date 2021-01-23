package neilyich.servers.tournamentservice.model;

public enum BilliardType {
    POOL, SNOOKER, PYRAMID, CAROM;

    public static BilliardType of(String str) {
        str = str.toLowerCase();
        if(str.contains("пирамид")
                || str.contains("pyramid")
                || str.contains("русск")
                || str.contains("пірамід")) {
            return PYRAMID;
        }
        if(str.contains("пул")
                || str.contains("американ")) {
            return POOL;
        }
        if(str.contains("snooker") || str.contains("снукер")) {
            return SNOOKER;
        }
        if(str.contains("carom") || str.contains("карамбол")) {
            return CAROM;
        }
        return null;
    }
}
