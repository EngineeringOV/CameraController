package ventures.of.util;

public enum NumberNotations {
    MATH_NOTATION_SUFFIXES(new NotationMap<Long, String>() {
        @Override
        public void setupMap(){
            this.put(1_000L, " K");
            this.put(1_000_000L, " M");
            this.put(1_000_000_000L, " G");
            this.put(1_000_000_000_000L, " T");
            this.put(1_000_000_000_000_000L, " P");
            this.put(1_000_000_000_000_000_000L, " E");}
    }),
    MS_TIME_NOTATION_SUFFIXES(new NotationMap<Long, String>() {
        @Override
        public void setupMap(){
            this.put(1L, " mS");
            this.put(1_000L, " S");
            this.put(60_000L, " M");
            this.put(3_600_000L, " H");
            this.put(86_400_000L, " D");
            this.put(604_800_000L, " W");}
    }),
    NONE;

    NotationMap<Long, String> notationMap;
    NumberNotations() {}
    NumberNotations(NotationMap<Long, String> notationMap) {
        this.notationMap = notationMap;
    }

    public NotationMap<Long, String> getMap(){
        return notationMap;
    }
}
