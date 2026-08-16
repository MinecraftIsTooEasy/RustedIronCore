package moddedmite.rustedironcore.localization.internal;

import moddedmite.rustedironcore.localization.LocalizationEnum;

public enum RICText implements LocalizationEnum {
    STATEMENT("ric.statement"),
    GUI_TITLE("ric.gui_title"),
    STATEMENT_COUNTDOWN("ric.statement_countdown"),
    ;

    private final String key;

    RICText(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
