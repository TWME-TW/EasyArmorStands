package me.m56738.easyarmorstands.menu.slot;

import me.m56738.easyarmorstands.api.menu.MenuClick;
import me.m56738.easyarmorstands.api.menu.MenuSlot;
import me.m56738.easyarmorstands.item.ItemTemplate;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class BackgroundSlot implements MenuSlot {
    private final ItemTemplate itemTemplate;
    private final TagResolver resolver;

    public BackgroundSlot(ItemTemplate itemTemplate, TagResolver resolver) {
        this.itemTemplate = itemTemplate;
        this.resolver = resolver;
    }

    @Override
    public ItemStack getItem(Locale locale) {
        return itemTemplate.render(locale, resolver);
    }

    @Override
    public void onClick(MenuClick click) {
        if (click.isRightClick() && click.cursor().getType() == Material.AIR) {
            click.close();
        }
    }
}