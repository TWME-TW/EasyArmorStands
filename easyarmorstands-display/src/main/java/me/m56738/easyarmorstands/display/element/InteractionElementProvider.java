package me.m56738.easyarmorstands.display.element;

import me.m56738.easyarmorstands.api.element.Element;
import me.m56738.easyarmorstands.api.element.EntityElementProvider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractionElementProvider implements EntityElementProvider {
    private final InteractionElementType type;

    public InteractionElementProvider(InteractionElementType type) {
        this.type = type;
    }

    @Override
    public @Nullable Element getElement(@NotNull Entity entity) {
        if (entity instanceof Interaction) {
            return type.getElement((Interaction) entity);
        }
        return null;
    }
}
