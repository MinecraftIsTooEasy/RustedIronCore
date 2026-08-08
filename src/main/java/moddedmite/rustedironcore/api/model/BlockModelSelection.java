package moddedmite.rustedironcore.api.model;

/** Resolved model and blockstate variant selected for one render pass. */
record BlockModelSelection(JsonBlockModel model, JsonBlockState.Variant variant) {
}
