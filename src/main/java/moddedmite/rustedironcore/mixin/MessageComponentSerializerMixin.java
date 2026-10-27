package moddedmite.rustedironcore.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import moddedmite.rustedironcore.internal.chat.MessageComponentJsonCompatibility;
import net.minecraft.ChatMessageComponent;
import net.minecraft.MessageComponentSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(MessageComponentSerializer.class)
public class MessageComponentSerializerMixin {

    @Inject(method = "deserializeComponent", at = @At("HEAD"), cancellable = true)
    private void deserializeModernRoot(JsonElement json, Type type, JsonDeserializationContext context,
                                       CallbackInfoReturnable<ChatMessageComponent> cir) {
        if (json.isJsonPrimitive()) {
            if (!json.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("A text component primitive must be a string");
            }
            cir.setReturnValue(ChatMessageComponent.createFromText(json.getAsString()));
            this.ric$attachTextComponent(cir.getReturnValue(), json);
            return;
        }
        if (!json.isJsonArray()) {
            return;
        }

        if (json.getAsJsonArray().size() == 0) throw new JsonParseException("Text component arrays cannot be empty");
        ChatMessageComponent component = new ChatMessageComponent();
        for (JsonElement child : json.getAsJsonArray()) {
            if (child.isJsonPrimitive() && child.getAsJsonPrimitive().isString()) {
                component.appendComponent(ChatMessageComponent.createFromText(child.getAsString()));
            } else if (child.isJsonArray() || child.isJsonObject()) {
                component.appendComponent(((MessageComponentSerializer) (Object) this)
                        .deserializeComponent(child, type, context));
            } else {
                throw new JsonParseException("Invalid text component array element: " + child);
            }
        }
        cir.setReturnValue(component);
        this.ric$attachTextComponent(component, json);
    }

    @Redirect(method = "deserializeComponent", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;get(Ljava/lang/String;)Lcom/google/gson/JsonElement;"))
    private JsonElement readModernComponentProperties(JsonObject object, String name) {
        return MessageComponentJsonCompatibility.readProperty(object, name);
    }

    @Inject(method = "deserializeComponent", at = @At("RETURN"))
    private void readClickEvent(JsonElement json, Type type, JsonDeserializationContext context,
                                CallbackInfoReturnable<ChatMessageComponent> cir) {
        ChatMessageComponent component = cir.getReturnValue();
        this.ric$attachTextComponent(component, json);
        if (!json.isJsonObject()) return;
        JsonObject object = json.getAsJsonObject();
        ChatClickEvent clickEvent = MessageComponentJsonCompatibility.readClickEvent(object);
        if (clickEvent != null) ((ChatClickComponentAccess) component).ric$setClickEvent(clickEvent);
        this.ric$readExtra(object, type, context, component);
        JsonElement insertion = object.get("insertion");
        if (insertion != null && insertion.isJsonPrimitive()) {
            ((ChatClickComponentAccess) component).ric$setInsertion(insertion.getAsString());
        }
    }

    @Unique
    private void ric$attachTextComponent(ChatMessageComponent component, JsonElement json) {
        try {
            ((ChatClickComponentAccess) component).ric$setTextComponent(TextComponentSerializer.fromJson(json));
        } catch (RuntimeException ignored) {
            ((ChatClickComponentAccess) component).ric$setTextComponent(null);
        }
    }

    @Unique
    private void ric$readExtra(JsonObject object, Type type, JsonDeserializationContext context,
                               ChatMessageComponent component) {
        JsonElement extra = object.get("extra");
        if (extra == null || !extra.isJsonArray()) {
            return;
        }
        for (JsonElement childJson : extra.getAsJsonArray()) {
            if (childJson.isJsonPrimitive()) {
                component.appendComponent(ChatMessageComponent.createFromText(childJson.getAsString()));
            } else if (childJson.isJsonObject() || childJson.isJsonArray()) {
                component.appendComponent(((MessageComponentSerializer) (Object) this)
                        .deserializeComponent(childJson, type, context));
            }
        }
    }

    @Inject(method = "serializeComponent", at = @At("RETURN"))
    private void writeClickEvent(ChatMessageComponent component, Type type, JsonSerializationContext context,
                                 CallbackInfoReturnable<JsonElement> cir) {
        if (!cir.getReturnValue().isJsonObject()) {
            return;
        }
        JsonObject object = cir.getReturnValue().getAsJsonObject();
        ChatClickComponentAccess access = (ChatClickComponentAccess) component;
        if (access.ric$getTextComponent() != null) {
            cir.setReturnValue(TextComponentSerializer.toJsonElement(access.ric$getTextComponent()));
            return;
        }
        MessageComponentJsonCompatibility.writeLegacyFields(object, access);
    }

    @Redirect(method = "serializeComponentChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/ChatMessageComponent;getText()Ljava/lang/String;"))
    private String preserveChildComponent(ChatMessageComponent child) {
        return null;
    }
}
