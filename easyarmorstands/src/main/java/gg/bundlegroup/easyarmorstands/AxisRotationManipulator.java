package gg.bundlegroup.easyarmorstands;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.awt.*;

public abstract class AxisRotationManipulator extends AxisManipulator {
    private final Matrix3d current = new Matrix3d();
    private final Vector3d lastDirection = new Vector3d();
    private final Vector3d currentDirection = new Vector3d();
    private boolean valid;

    public AxisRotationManipulator(Session session, Vector3dc axis, Color color) {
        super(session, axis, color);
    }

    private void updateDirection(Vector3d dest) {
        getSession().getCursor().get().sub(getAxisPoint(), dest);
    }

    protected Matrix3d getRotation() {
        return current;
    }

    protected abstract Vector3dc getAnchor();

    protected abstract void refreshRotation();

    protected abstract void onRotate(double angle);

    @Override
    protected void start(Vector3d origin, Vector3d axisDirection) {
        refreshRotation();
        origin.set(getAnchor());
        current.transform(getAxis(), axisDirection).normalize();
        updateAxisPoint();
        updateDirection(lastDirection);
        valid = false;
    }

    @Override
    public void update() {
        super.update();
        if (getSession().getPlayer().platform().canSpawnParticles()) {
            getSession().getPlayer().showLine(getAxisPoint(), getSession().getCursor().get(), Color.WHITE, false);
        }

        Vector3dc axisDirection = getAxisDirection();
        updateDirection(currentDirection);
        double angle = lastDirection.angleSigned(currentDirection, axisDirection);
        lastDirection.set(currentDirection);

        if (!valid || !Double.isFinite(angle)) {
            if (currentDirection.lengthSquared() > 0.05) {
                valid = true;
            }
            return;
        }

        current.rotateLocal(angle, axisDirection.x(), axisDirection.y(), axisDirection.z());
        onRotate(angle);
    }
}
