package me.m56738.easyarmorstands.bone.v1_19_4;

import me.m56738.easyarmorstands.addon.display.DisplayAddon;
import me.m56738.easyarmorstands.bone.EntityLocationBone;
import me.m56738.easyarmorstands.bone.RotationBone;
import me.m56738.easyarmorstands.bone.ScaleBone;
import me.m56738.easyarmorstands.property.EntityProperty;
import me.m56738.easyarmorstands.property.v1_19_4.display.DisplayScaleProperty;
import me.m56738.easyarmorstands.property.v1_19_4.display.DisplayTranslationProperty;
import me.m56738.easyarmorstands.session.Session;
import org.bukkit.entity.Display;
import org.joml.Math;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class DisplayBone extends EntityLocationBone implements RotationBone, ScaleBone {
    private final Session session;
    private final Display entity;
    private final DisplayTranslationProperty translationProperty;
    private final EntityProperty<Display, Quaternionfc> rotationProperty;
    private final DisplayScaleProperty scaleProperty;

    public DisplayBone(Session session, Display entity, DisplayAddon addon, EntityProperty<Display, Quaternionfc> rotationProperty) {
        super(session, entity);
        this.session = session;
        this.entity = entity;
        this.translationProperty = addon.getDisplayTranslationProperty();
        this.rotationProperty = rotationProperty;
        this.scaleProperty = addon.getDisplayScaleProperty();
    }

    @Override
    public boolean isValid() {
        return entity.isValid();
    }

    @Override
    public Vector3dc getOffset() {
        return new Vector3d(translationProperty.getValue(entity))
                .rotateY(-Math.toRadians(entity.getLocation().getYaw()));
    }

    @Override
    public Vector3dc getAnchor() {
        return getPosition();
    }

    @Override
    public Vector3dc getOrigin() {
        return getPosition();
    }

    @Override
    public Quaterniondc getRotation() {
        return new Quaterniond(rotationProperty.getValue(entity))
                .rotateLocalY(-Math.toRadians(entity.getLocation().getYaw()));
    }

    @Override
    public void setRotation(Quaterniondc rotation) {
        session.setProperty(entity, rotationProperty, new Quaternionf(rotation)
                .rotateLocalY(Math.toRadians(entity.getLocation().getYaw())));
    }

    @Override
    public Vector3dc getScale() {
        return new Vector3d(scaleProperty.getValue(entity));
    }

    @Override
    public void setScale(Vector3dc scale) {
        session.setProperty(entity, scaleProperty, scale.get(new Vector3f()));
    }
}