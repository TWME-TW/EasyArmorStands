package me.m56738.easyarmorstands.editor.button;

import me.m56738.easyarmorstands.api.editor.EyeRay;
import me.m56738.easyarmorstands.api.editor.Session;
import me.m56738.easyarmorstands.api.editor.button.Button;
import me.m56738.easyarmorstands.api.editor.button.ButtonResult;
import me.m56738.easyarmorstands.api.particle.BoundingBoxParticle;
import me.m56738.easyarmorstands.api.particle.ParticleColor;
import me.m56738.easyarmorstands.api.particle.PointParticle;
import me.m56738.easyarmorstands.api.util.BoundingBox;
import me.m56738.easyarmorstands.util.Util;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.function.Consumer;

public abstract class BoundingBoxButton implements Button {
    private final Session session;
    private final Vector3d position = new Vector3d();
    private final Quaterniond rotation = new Quaterniond();
    private final PointParticle pointParticle;
    private final BoundingBoxParticle boxParticle;
    private BoundingBox box;
    private boolean previewVisible;
    private boolean boxVisible;

    public BoundingBoxButton(Session session) {
        this.session = session;
        this.pointParticle = session.particleProvider().createPoint();
        this.pointParticle.setBillboard(false);
        this.boxParticle = session.particleProvider().createAxisAlignedBox();
    }

    protected abstract Vector3dc getPosition();

    protected Quaterniondc getRotation() {
        return Util.IDENTITY;
    }

    protected abstract BoundingBox getBoundingBox();

    @Override
    public void update() {
        box = BoundingBox.of(getBoundingBox());
        position.set(getPosition());
        rotation.set(getRotation());
    }

    @Override
    public void intersect(@NotNull EyeRay ray, @NotNull Consumer<@NotNull ButtonResult> results) {
        Vector3dc intersection = ray.intersectPoint(position);
        if (intersection != null) {
            results.accept(ButtonResult.of(intersection));
        }
        intersection = ray.intersectBox(box);
        if (intersection != null) {
            results.accept(ButtonResult.of(intersection, -1));
        }
    }

    @Override
    public void updatePreview(boolean focused) {
        pointParticle.setPosition(position);
        pointParticle.setRotation(rotation);
        pointParticle.setColor(focused ? ParticleColor.YELLOW : ParticleColor.WHITE);
        boxParticle.setBoundingBox(box);
        boolean showBox = focused && !box.getMinPosition().equals(box.getMaxPosition());
        if (previewVisible && showBox != boxVisible) {
            if (showBox) {
                session.addParticle(boxParticle);
            } else {
                session.removeParticle(boxParticle);
            }
        }
        boxVisible = showBox;
    }

    @Override
    public void showPreview() {
        previewVisible = true;
        session.addParticle(pointParticle);
        if (boxVisible) {
            session.addParticle(boxParticle);
        }
    }

    @Override
    public void hidePreview() {
        previewVisible = false;
        session.removeParticle(pointParticle);
        if (boxVisible) {
            session.removeParticle(boxParticle);
        }
    }
}
