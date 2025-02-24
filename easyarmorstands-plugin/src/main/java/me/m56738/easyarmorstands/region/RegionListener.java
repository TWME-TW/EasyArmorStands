package me.m56738.easyarmorstands.region;

import me.m56738.easyarmorstands.api.element.Element;
import me.m56738.easyarmorstands.api.element.EntityElement;
import me.m56738.easyarmorstands.api.event.player.PlayerCreateElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerDestroyElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerDiscoverElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerEditElementEvent;
import me.m56738.easyarmorstands.api.event.player.PlayerEditPropertyEvent;
import me.m56738.easyarmorstands.api.event.session.SessionStartEvent;
import me.m56738.easyarmorstands.api.property.type.EntityPropertyTypes;
import me.m56738.easyarmorstands.api.region.RegionPrivilegeChecker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.WeakHashMap;

public class RegionListener implements Listener {
    private final Map<Player, Boolean> bypassCache = new WeakHashMap<>();
    private final RegionPrivilegeChecker privilegeChecker;

    public RegionListener(RegionPrivilegeChecker privilegeChecker) {
        this.privilegeChecker = privilegeChecker;
    }

    private boolean isAllowed(Player player, Location location) {
        return privilegeChecker.isAllowed(player, location);
    }

    @EventHandler
    public void onInitialize(SessionStartEvent event) {
        bypassCache.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDiscover(PlayerDiscoverElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(EntityPropertyTypes.LOCATION).getValue())) {
            return;
        }
        if (canBypass(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEdit(PlayerEditElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(EntityPropertyTypes.LOCATION).getValue())) {
            return;
        }
        if (canBypass(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
        privilegeChecker.sendEditError(event.getPlayer(), event.getElement());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(PlayerCreateElementEvent event) {
        if (isAllowed(event.getPlayer(), event.getProperties().get(EntityPropertyTypes.LOCATION).getValue())) {
            return;
        }
        if (canBypass(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
        privilegeChecker.sendCreateError(event.getPlayer(), event.getProperties());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEdit(PlayerEditPropertyEvent<?> event) {
        Element element = event.getElement();
        if (!(element instanceof EntityElement<?>)) {
            return;
        }
        Entity entity = ((EntityElement<?>) element).getEntity();
        if (isAllowed(event.getPlayer(), entity.getLocation())) {
            if (event.getProperty().getType() != EntityPropertyTypes.LOCATION) {
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
        if (isAllowed(event.getPlayer(), event.getElement().getProperties().get(EntityPropertyTypes.LOCATION).getValue())) {
            return;
        }
        if (bypassCache.computeIfAbsent(event.getPlayer(), this::canBypass)) {
            return;
        }
        event.setCancelled(true);
        privilegeChecker.sendDestroyError(event.getPlayer(), event.getElement());
    }

    private boolean canBypass(Player player) {
        boolean bypass = privilegeChecker.canBypass(player);
        bypassCache.put(player, bypass);
        return bypass;
    }
}
