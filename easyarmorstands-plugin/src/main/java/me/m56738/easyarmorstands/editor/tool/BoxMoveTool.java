package me.m56738.easyarmorstands.editor.tool;

import me.m56738.easyarmorstands.api.editor.Snapper;
import me.m56738.easyarmorstands.api.editor.tool.MoveTool;
import me.m56738.easyarmorstands.api.editor.tool.MoveToolSession;
import me.m56738.easyarmorstands.api.util.PositionProvider;
import me.m56738.easyarmorstands.api.util.RotationProvider;
import me.m56738.easyarmorstands.editor.box.BoundingBoxEditor;
import me.m56738.easyarmorstands.editor.box.BoundingBoxEditorSession;
import me.m56738.easyarmorstands.message.Message;
import me.m56738.easyarmorstands.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BoxMoveTool implements MoveTool {
    private final BoundingBoxEditor editor;
    private final PositionProvider positionProvider;
    private final RotationProvider rotationProvider;

    public BoxMoveTool(BoundingBoxEditor editor, PositionProvider positionProvider, RotationProvider rotationProvider) {
        this.editor = editor;
        this.positionProvider = positionProvider;
        this.rotationProvider = rotationProvider;
    }

    @Override
    public @NotNull Vector3dc getPosition() {
        return positionProvider.getPosition();
    }

    @Override
    public @NotNull Quaterniondc getRotation() {
        return rotationProvider.getRotation();
    }

    @Override
    public @NotNull MoveToolSession start() {
        return new SessionImpl(editor.start());
    }

    @Override
    public boolean canUse(@NotNull Player player) {
        return editor.canMove(player);
    }

    private static class SessionImpl implements MoveToolSession {
        private final BoundingBoxEditorSession session;
        private final Vector3dc center;
        private final Vector3d offset = new Vector3d();

        public SessionImpl(BoundingBoxEditorSession session) {
            this.session = session;
            this.center = session.getBoundingBox().getCenter(new Vector3d());
        }

        @Override
        public void setChange(@NotNull Vector3dc change) {
            this.offset.set(change);
            session.setCenter(center.add(change, new Vector3d(offset)));
        }

        @Override
        public void snapChange(@NotNull Vector3d change, @NotNull Snapper context) {
            change.add(center);
            context.snapPosition(change);
            change.sub(center);
        }

        @Override
        public @NotNull Vector3dc getPosition() {
            return session.getBoundingBox().getCenter(new Vector3d());
        }

        @Override
        public void setPosition(@NotNull Vector3dc position) {
            setChange(position.sub(center, offset));
        }

        @Override
        public void revert() {
            session.revert();
        }

        @Override
        public void commit(@Nullable Component description) {
            session.commit(description);
        }

        @Override
        public boolean isValid() {
            return session.isValid();
        }

        @Override
        public @Nullable Component getStatus() {
            return Util.formatOffset(offset);
        }

        @Override
        public @Nullable Component getDescription() {
            return Message.component("easyarmorstands.history.move-box", Util.formatOffset(offset));
        }
    }
}
