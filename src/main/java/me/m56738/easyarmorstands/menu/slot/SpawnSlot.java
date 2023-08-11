package me.m56738.easyarmorstands.menu.slot;

import me.m56738.easyarmorstands.EasyArmorStands;
import me.m56738.easyarmorstands.history.action.EntitySpawnAction;
import me.m56738.easyarmorstands.menu.MenuClick;
import me.m56738.easyarmorstands.node.EntitySelectionNode;
import me.m56738.easyarmorstands.session.EntitySpawner;
import me.m56738.easyarmorstands.session.Session;
import me.m56738.easyarmorstands.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class SpawnSlot implements MenuSlot {
    private final EntitySpawner<?> spawner;
    private final ItemStack item;

    public SpawnSlot(EntitySpawner<?> spawner, ItemStack item) {
        this.spawner = spawner;
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void onClick(MenuClick click) {
        click.cancel();
        if (click.isLeftClick()) {
            Player player = click.player();
            Location eyeLocation = player.getEyeLocation();
            Vector3d cursor = Util.getRotation(eyeLocation, new Matrix3d()).transform(0, 0, 2, new Vector3d());
            Vector3d position = new Vector3d(cursor);
            if (!player.isFlying()) {
                position.y = 0;
            }
            Session session = EasyArmorStands.getInstance().getSessionManager().getSession(player);
            Location location = player.getLocation().add(position.x, position.y, position.z);
            location.setYaw(location.getYaw() + 180);
            if (session != null) {
                location.setX(session.snap(location.getX()));
                location.setY(session.snap(location.getY()));
                location.setZ(session.snap(location.getZ()));
                location.setYaw((float) session.snapAngle(location.getYaw()));
                location.setPitch((float) session.snapAngle(location.getPitch()));
            }
            Entity entity = EntitySpawner.trySpawn(spawner, location, player);
            if (entity == null) {
                return;
            }
            EntitySpawnAction<?> action = new EntitySpawnAction<>(location, spawner, entity.getUniqueId());
            EasyArmorStands.getInstance().getHistory(player).push(action);

            if (session != null) {
                EntitySelectionNode selectionNode = session.findNode(EntitySelectionNode.class);
                if (selectionNode != null) {
                    selectionNode.selectEntity(entity);
                }
            }

            click.close();
        }
    }
}
