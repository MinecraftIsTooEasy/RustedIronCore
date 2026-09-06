package moddedmite.rustedironcore.api.model;

public enum JsonModelDisplayContext {
    BLOCK("fixed"),
    BLOCK_ITEM("fixed"),
    NONE("none"),
    GUI("gui"),
    GROUND("ground"),
    FIXED("fixed"),
    HEAD("head"),
    FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
    FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
    THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
    THIRD_PERSON_LEFT_HAND("thirdperson_lefthand");

    private final String jsonName;

    JsonModelDisplayContext(String jsonName) {
        this.jsonName = jsonName;
    }

    public String getJsonName() {
        return this.jsonName;
    }

    public boolean isFirstPerson() {
        return this == FIRST_PERSON_RIGHT_HAND || this == FIRST_PERSON_LEFT_HAND;
    }
}
