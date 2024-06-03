package lab.tools.subjects.enums;

/**
 * Enum representing the venue`s type.
 */
public enum VenueType {

    BAR("BAR"),
    CINEMA("CINEMA"),
    MALL("MALL"),
    STADIUM("STADIUM");
    public String name;
    VenueType(String name){
        this.name= name;
    }
    @Override
    public String toString(){
        return name;
    }


}

