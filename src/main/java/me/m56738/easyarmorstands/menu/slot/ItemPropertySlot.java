package me.m56738.easyarmorstands.menu.slot;

import me.m56738.easyarmorstands.menu.MenuClick;
import me.m56738.easyarmorstands.property.Property;
import me.m56738.easyarmorstands.property.PropertyContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemPropertySlot implements MenuSlot {
    private final Property<ItemStack> property;
    private final PropertyContainer container;

    public ItemPropertySlot(Property<ItemStack> property, PropertyContainer container) {
        this.property = property;
        this.container = container;
    }

    @Override
    public ItemStack getItem() {
        return property.getValue();
    }

    @Override
    public void onClick(MenuClick click) {
        String permission = property.getType().getPermission();
        if (permission != null && !click.player().hasPermission(permission)) {
            click.cancel();
            return;
        }
        click.queueTask(item -> {
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            property.setValue(item);
            container.commit();
        });
    }
}
