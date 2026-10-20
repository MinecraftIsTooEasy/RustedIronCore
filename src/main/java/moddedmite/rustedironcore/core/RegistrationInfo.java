package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.annotations.NotFinished;

@NotFinished
public record RegistrationInfo() {
    public static final RegistrationInfo BUILT_IN = new RegistrationInfo();
}
