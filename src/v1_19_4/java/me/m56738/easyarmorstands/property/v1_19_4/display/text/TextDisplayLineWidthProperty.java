package me.m56738.easyarmorstands.property.v1_19_4.display.text;

import io.leangen.geantyref.TypeToken;
import me.m56738.easyarmorstands.property.LegacyEntityPropertyType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextDisplayLineWidthProperty implements LegacyEntityPropertyType<TextDisplay, Integer> {
    @Override
    public Integer getValue(TextDisplay entity) {
        return entity.getLineWidth();
    }

    @Override
    public TypeToken<Integer> getValueType() {
        return TypeToken.get(Integer.class);
    }

    @Override
    public void setValue(TextDisplay entity, Integer value) {
        entity.setLineWidth(value);
    }

    @Override
    public @NotNull String getName() {
        return "linewidth";
    }

    @Override
    public @NotNull Class<TextDisplay> getEntityType() {
        return TextDisplay.class;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.text("line width");
    }

    @Override
    public @NotNull Component getValueName(Integer value) {
        return Component.text(value);
    }

    @Override
    public @Nullable String getPermission() {
        return "easyarmorstands.property.display.text.linewidth";
    }
}
