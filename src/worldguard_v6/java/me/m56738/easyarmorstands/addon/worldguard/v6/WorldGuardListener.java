package me.m56738.easyarmorstands.addon.worldguard.v6;

import com.sk89q.worldguard.bukkit.WGBukkit;
import me.m56738.easyarmorstands.EasyArmorStands;
import me.m56738.easyarmorstands.api.element.Element;
import me.m56738.easyarmorstands.api.element.EntityElement;
import me.m56738.easyarmorstands.api.event.player.PlayerCreateElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerDestroyElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerEditPropertyEvent;
import me.m56738.easyarmorstands.api.event.session.SessionSelectElementEvent;
import me.m56738.easyarmorstands.api.event.session.SessionStartEvent;
import me.m56738.easyarmorstands.api.property.type.PropertyTypes;
import me.m56738.easyarmorstands.message.Message;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.WeakHashMap;

public class WorldGuardListener implements Listener {
    private final Map<Player, Boolean> bypassCache = new WeakHashMap<>();
    private final String bypassPermission = "easyarmorstands.worldguard.bypass";

    public WorldGuardListener() {
    }

    private static Audience audience(Player player) {
        return EasyArmorStands.getInstance().getAdventure().player(player);
    }

    private boolean isAllowed(Player player, Location location) {
        return WGBukkit.getPlugin().canBuild(player, location);
    }

    @EventHandler
    public void onInitialize(SessionStartEvent event) {
        bypassCache.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSelect(SessionSelectElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(PropertyTypes.ENTITY_LOCATION).getValue())) {
            return;
        }
        if (event.getPlayer().hasPermission(bypassPermission)) {
            return;
        }
        event.setCancelled(true);
        audience(event.getPlayer()).sendMessage(Message.error("easyarmorstands.error.worldguard.deny-select"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(PlayerCreateElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getProperties().get(PropertyTypes.ENTITY_LOCATION).getValue())) {
            return;
        }
        if (event.getPlayer().hasPermission(bypassPermission)) {
            return;
        }
        event.setCancelled(true);
        audience(event.getPlayer()).sendMessage(Message.error("easyarmorstands.error.worldguard.deny-create"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEdit(PlayerEditPropertyEvent<?> event) {
        Element element = event.getElement();
        if (!(element instanceof EntityElement<?>)) {
            return;
        }
        Entity entity = ((EntityElement<?>) element).getEntity();
        if (isAllowed(event.getPlayer(), entity.getLocation())) {
            if (event.getProperty().getType() != PropertyTypes.ENTITY_LOCATION) {
                return;
            }
            if (isAllowed(event.getPlayer(), (org.bukkit.Location) event.getNewValue())) {
                return;
            }
        }
        if (bypassCache.computeIfAbsent(event.getPlayer(), this::canBypass)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDestroy(PlayerDestroyElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(PropertyTypes.ENTITY_LOCATION).getValue())) {
            return;
        }
        if (bypassCache.computeIfAbsent(event.getPlayer(), this::canBypass)) {
            return;
        }
        event.setCancelled(true);
        audience(event.getPlayer()).sendMessage(Message.error("easyarmorstands.error.worldguard.deny-destroy"));
    }

    private boolean canBypass(Player player) {
        return player.hasPermission(bypassPermission);
    }
}
