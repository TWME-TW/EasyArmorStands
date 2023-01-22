package gg.bundlegroup.easyarmorstands.common.session;

import gg.bundlegroup.easyarmorstands.common.handle.Handle;
import gg.bundlegroup.easyarmorstands.common.handle.PositionHandle;
import gg.bundlegroup.easyarmorstands.common.manipulator.Manipulator;
import gg.bundlegroup.easyarmorstands.common.platform.EasArmorEntity;
import gg.bundlegroup.easyarmorstands.common.platform.EasArmorStand;
import gg.bundlegroup.easyarmorstands.common.platform.EasFeature;
import gg.bundlegroup.easyarmorstands.common.platform.EasItem;
import gg.bundlegroup.easyarmorstands.common.platform.EasPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.joml.Vector3d;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private static final double RANGE = 5;
    private final EasPlayer player;
    private final EasArmorStand entity;
    private final EasArmorStand skeleton;
    private final Map<String, Handle> handles = new HashMap<>();

    private int clickTicks = 5;
    private Handle handle;
    private boolean active;

    public Session(EasPlayer player, EasArmorStand entity) {
        this.player = player;
        this.entity = entity;
        if (player.platform().hasFeature(EasFeature.ENTITY_GLOW)) {
            this.skeleton = entity.getWorld().spawnArmorStand(entity.getPosition(), entity.getYaw(), e -> {
                e.setVisible(false);
                e.setBasePlate(false);
                e.setArms(true);
                e.setPersistent(false);
                e.setGravity(false);
                e.setCanTick(false);
                updateSkeleton(e);
                for (EasPlayer other : player.platform().getPlayers()) {
                    if (!player.equals(other)) {
                        other.hideEntity(e);
                    }
                }
                e.setGlowing(true);
            });
        } else {
            this.skeleton = null;
        }
    }

    public void addHandle(String name, Handle handle) {
        handles.put(name, handle);
    }

    private boolean handleClick() {
        if (clickTicks > 0) {
            return false;
        }
        clickTicks = 5;
        return true;
    }

    public void handleLeftClick() {
        if (!handleClick()) {
            return;
        }
        setHandle(null);
    }

    public void handleRightClick() {
        if (!handleClick()) {
            return;
        }
        update();
        if (handle != null) {
            if (active) {
                handle.click();
            }
            active = true;
        }
    }

    public boolean update() {
        if (clickTicks > 0) {
            clickTicks--;
        }

        player.update();
        entity.update();

        if (active) {
            handle.update(true);
        } else {
            updateTargetHandle();
        }

        if (skeleton != null) {
            updateSkeleton(skeleton);
        }

        if (handle != null) {
            player.showTitle(Title.title(handle.title(), handle.subtitle(),
                    Title.Times.times(Duration.ZERO, Ticks.duration(20), Duration.ZERO)));
        } else {
            player.clearTitle();
        }

        return player.isValid() && entity.isValid() && (skeleton == null || skeleton.isValid()) &&
                player.getEyePosition().distanceSquared(entity.getPosition()) < 100 * 100;
    }

    private void updateTargetHandle() {
        Handle bestHandle = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        Vector3d temp = new Vector3d();
        for (Handle candidate : handles.values()) {
            candidate.update(false);
            player.showPoint(candidate.getPosition(), NamedTextColor.WHITE);
            candidate.getPosition().sub(player.getEyePosition(), temp).mulTranspose(player.getEyeRotation());
            double distance = temp.z;
            // Eliminate forward part
            temp.z = 0;
            // Distance from straight line
            double deviationSquared = temp.lengthSquared();
            if (deviationSquared < 0.025) {
                if (distance > 0 && distance < bestDistance && distance < RANGE) {
                    bestHandle = candidate;
                    bestDistance = distance;
                }
            }
        }
        handle = bestHandle;
    }

    public void stop() {
        if (skeleton != null) skeleton.remove();
        player.clearTitle();
    }

    private void updateSkeleton(EasArmorStand skeleton) {
        skeleton.teleport(entity.getPosition(), entity.getYaw(), 0);
        skeleton.setSmall(entity.isSmall());
        Vector3d pose = new Vector3d();
        for (EasArmorStand.Part part : EasArmorStand.Part.values()) {
            skeleton.setPose(part, entity.getPose(part, pose));
        }
    }

    public void setHandle(Handle handle) {
        if (handle != null) {
            this.handle = handle;
            this.active = true;
        } else {
            this.active = false;
        }
    }

    public void setHandle(Handle handle, Manipulator manipulator) {
        setHandle(handle);
        handle.update(false);
        handle.select(manipulator);
    }

    public EasArmorStand getEntity() {
        return entity;
    }

    public EasPlayer getPlayer() {
        return player;
    }

    public void startMoving() {
        player.update();
        entity.update();
        for (Handle value : handles.values()) {
            if (value instanceof PositionHandle) {
                setHandle(value);
                return;
            }
        }
    }

    public void openMenu() {
        SessionInventory inventory = new SessionInventory(this, player.platform(),
                Component.text("Equipment"));
        player.openInventory(inventory.getInventory());
    }

    public boolean isToolInOffHand() {
        EasItem item = player.getItem(EasArmorEntity.Slot.OFF_HAND);
        if (item == null) {
            return false;
        }
        return item.isTool();
    }

    public void hideSkeleton(EasPlayer player) {
        if (skeleton != null) {
            player.hideEntity(skeleton);
        }
    }

    public Map<String, Handle> getHandles() {
        return Collections.unmodifiableMap(handles);
    }
}
