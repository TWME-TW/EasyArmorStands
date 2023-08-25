package me.m56738.easyarmorstands.config.serializer;

import org.bukkit.Material;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class MaterialSerializer extends ScalarSerializer<Material> {
    public MaterialSerializer() {
        super(Material.class);
    }

    @Override
    public Material deserialize(Type type, Object value) throws SerializationException {
        String name = value.toString();
        Material material = Material.matchMaterial(name);
        if (material == null) {
            throw new SerializationException("Unknown material: " + name);
        }
        return material;
    }

    @Override
    protected Object serialize(Material item, Predicate<Class<?>> typeSupported) {
        return item.name();
    }
}
