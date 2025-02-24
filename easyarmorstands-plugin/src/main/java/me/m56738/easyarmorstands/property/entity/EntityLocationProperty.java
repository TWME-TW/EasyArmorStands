package me.m56738.easyarmorstands.property.entity;

import me.m56738.easyarmorstands.api.property.Property;
import me.m56738.easyarmorstands.api.property.type.EntityPropertyTypes;
import me.m56738.easyarmorstands.api.property.type.PropertyType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityLocationProperty implements Property<Location> {
    private final Entity entity;

    public EntityLocationProperty(Entity entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull PropertyType<Location> getType() {
        return EntityPropertyTypes.LOCATION;
    }

    @Override
    public @NotNull Location getValue() {
        return entity.getLocation();
    }

    @Override
    public boolean setValue(@NotNull Location value) {
        boolean ok = entity.teleport(value);
        if (ok) {
            entity.setFallDistance(0);
        }
        return ok;
    }
}
