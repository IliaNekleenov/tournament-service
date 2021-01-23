package neilyich.servers.tournamentservice.model;

public enum Category {
    WHEELCHAIR, JUNIOR_MEN, JUNIOR_WOMEN, JUNIOR_ALL, MEN, WOMEN, ALL, STUDENTS, ELDERLY;

    public static Category of(String str) {
        if(str.contains("инвалид") || str.contains("колясоч")) {
            return Category.WHEELCHAIR;
        }
        if(str.contains("ветеран") || str.contains("пожил")) {
            return Category.ELDERLY;
        }
        if(str.contains("u-cup") || str.contains("студен") || str.contains(" вуз")) {
            return Category.STUDENTS;
        }
        str = str.toLowerCase();
        boolean women = str.contains("девуш");
        boolean men = str.contains("мальчик") || str.contains("мужчин") || str.contains("юнош");
        if(str.contains("первенство") || str.contains("до 18") || str.contains("до 16") || str.contains("юниор") || str.contains("юнош")) {
            if(men && !women) {
                return Category.JUNIOR_MEN;
            }
            else if(!men && women) {
                return Category.JUNIOR_WOMEN;
            }
            else {
                return Category.JUNIOR_ALL;
            }
        }
        else {
            if(men && !women) {
                return Category.MEN;
            }
            else if(!men && women) {
                return Category.WOMEN;
            }
            else {
                return Category.ALL;
            }
        }
    }
}
