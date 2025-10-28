package moddedmite.rustedironcore.localization.internal;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import moddedmite.rustedironcore.localization.LocalizationEnum;

public enum RICText implements LocalizationEnum {
    Statement,
    GuiTitle,
    StatementCountdown,
    ;

    private static final Converter<String, String> CONVERTER = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

    @Override
    public String getKey() {
        return "ric." + CONVERTER.convert(this.name());
    }
}
