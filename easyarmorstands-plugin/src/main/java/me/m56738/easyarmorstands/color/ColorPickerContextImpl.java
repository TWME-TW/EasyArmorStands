package me.m56738.easyarmorstands.color;

import me.m56738.easyarmorstands.EasyArmorStandsPlugin;
import me.m56738.easyarmorstands.api.menu.ColorPickerContext;
import me.m56738.easyarmorstands.api.property.Property;
import me.m56738.easyarmorstands.api.property.PropertyContainer;
import me.m56738.easyarmorstands.capability.itemcolor.ItemColorCapability;
import me.m56738.easyarmorstands.menu.slot.ItemPropertySlot;
import me.m56738.easyarmorstands.message.Message;
import me.m56738.easyarmorstands.util.Util;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ColorPickerContextImpl implements ColorPickerContext {
    private final Property<ItemStack> property;
    private final PropertyContainer container;
    private final ItemColorCapability itemColorCapability;

    public ColorPickerContextImpl(Property<ItemStack> property, PropertyContainer container) {
        this.property = property;
        this.container = container;
        this.itemColorCapability = EasyArmorStandsPlugin.getInstance().getCapability(ItemColorCapability.class);
    }

    public ColorPickerContextImpl(ItemPropertySlot slot) {
        this(slot.getProperty(), slot.getContainer());
    }

    @Override
    public @NotNull ItemStack item() {
        return property.getValue();
    }

    @Override
    public @NotNull Color getColor() {
        ItemStack item = property.getValue();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Color.WHITE;
        }
        return itemColorCapability.getColor(meta);
    }

    @Override
    public void setColor(@NotNull Color color) {
        ItemStack item = property.getValue().clone();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (itemColorCapability.setColor(meta, color)) {
            item.setItemMeta(meta);
            property.setValue(item);
            container.commit(Message.component("easyarmorstands.history.changed-color", Util.formatColor(color)));
        }
    }
}
