package me.m56738.easyarmorstands.addon.worldguard.v6;

import com.sk89q.worldguard.bukkit.WGBukkit;
import me.m56738.easyarmorstands.EasyArmorStands;
import me.m56738.easyarmorstands.element.Element;
import me.m56738.easyarmorstands.element.EntityElement;
import me.m56738.easyarmorstands.event.PlayerCreateElementEvent;
import me.m56738.easyarmorstands.event.PlayerDestroyElementEvent;
import me.m56738.easyarmorstands.event.PlayerEditPropertyEvent;
import me.m56738.easyarmorstands.event.SessionSelectElementEvent;
import me.m56738.easyarmorstands.event.SessionStartEvent;
import me.m56738.easyarmorstands.message.Message;
import me.m56738.easyarmorstands.property.entity.EntityLocationProperty;
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
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(EntityLocationProperty.TYPE).getValue())) {
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
        if (isAllowed(event.getPlayer(), event.getProperties().get(EntityLocationProperty.TYPE).getValue())) {
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
            if (event.getProperty().getType() != EntityLocationProperty.TYPE) {
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
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(EntityLocationProperty.TYPE).getValue())) {
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
