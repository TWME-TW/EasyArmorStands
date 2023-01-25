package gg.bundlegroup.easyarmorstands.core.tool;

import gg.bundlegroup.easyarmorstands.core.bone.PositionBone;
import gg.bundlegroup.easyarmorstands.core.platform.EasArmorStand;
import gg.bundlegroup.easyarmorstands.core.platform.EasPlayer;
import gg.bundlegroup.easyarmorstands.core.session.Session;
import gg.bundlegroup.easyarmorstands.core.util.Cursor3D;
import gg.bundlegroup.easyarmorstands.core.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.joml.Intersectiond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PositionAxisTool extends AbstractTool {
    private final PositionBone bone;
    private final Session session;
    private final EasPlayer player;
    private final TextColor color;
    private final Vector3dc axis;

    private final Vector3d negativeHandle = new Vector3d();
    private final Vector3d positiveHandle = new Vector3d();
    private final Vector3d lookRayEnd = new Vector3d();
    private final Vector3d lookRayPoint = new Vector3d();
    private final Vector3d handlePoint = new Vector3d();
    private final Vector3d start = new Vector3d();
    private final Vector3d currentHandle = new Vector3d();
    private final Vector3d origin = new Vector3d();
    private final Vector3d temp = new Vector3d();
    private final Cursor3D cursor;
    private Vector3dc lookTarget;

    public PositionAxisTool(PositionBone bone, String name, RGBLike color, Vector3dc axis) {
        super(Component.text(name, TextColor.color(color)), Component.text("Move"));
        this.bone = bone;
        this.session = bone.session();
        this.player = bone.session().getPlayer();
        this.color = TextColor.color(color);
        this.axis = new Vector3d(axis);
        this.cursor = new Cursor3D(player);
    }

    @Override
    public void refresh() {
        bone.getPosition().fma(-2, axis, negativeHandle);
        bone.getPosition().fma(2, axis, positiveHandle);
        Vector3dc eyePosition = player.getEyePosition();
        player.getEyeRotation().transform(0, 0, session.getRange(), lookRayEnd).add(eyePosition);
        double d = Intersectiond.findClosestPointsLineSegments(
                eyePosition.x(), eyePosition.y(), eyePosition.z(),
                lookRayEnd.x(), lookRayEnd.y(), lookRayEnd.z(),
                negativeHandle.x, negativeHandle.y, negativeHandle.z,
                positiveHandle.x, positiveHandle.y, positiveHandle.z,
                lookRayPoint,
                handlePoint
        );
        double threshold = session.getLookThreshold();
        if (d < threshold * threshold) {
            lookTarget = lookRayPoint;
        } else {
            lookTarget = null;
        }
    }

    @Override
    public void start(Vector3dc cursor) {
        this.cursor.start(cursor, false);
        start.set(cursor);
        session.getEntity().getPosition().get(origin);
    }

    @Override
    public Component update() {
        cursor.update(false);
        double t = session.snap(cursor.get().sub(start, temp).dot(axis));
        origin.fma(t, axis, temp);
        if (!session.move(temp)) {
            return null;
        }
        start.fma(t, axis, currentHandle);
        bone.refresh();
        bone.getPosition().fma(-2, axis, negativeHandle);
        bone.getPosition().fma(2, axis, positiveHandle);
        return Component.text(Util.OFFSET_FORMAT.format(t), color);
    }

    @Override
    public void abort() {
        EasArmorStand entity = session.getEntity();
        entity.teleport(origin, entity.getYaw(), 0);
    }

    @Override
    public void showHandles() {
        player.showPoint(negativeHandle, color);
        player.showLine(negativeHandle, bone.getPosition(), color, false);
        player.showLine(bone.getPosition(), positiveHandle, color, false);
        player.showPoint(positiveHandle, color);
    }

    @Override
    public void show() {
        player.showLine(negativeHandle, positiveHandle, color, true);
        player.showLine(currentHandle, cursor.get(), NamedTextColor.WHITE, false);
    }

    @Override
    public Vector3dc getTarget() {
        return bone.getPosition();
    }

    @Override
    public Vector3dc getLookTarget() {
        return lookTarget;
    }
}