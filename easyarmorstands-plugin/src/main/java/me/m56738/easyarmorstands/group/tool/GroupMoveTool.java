package me.m56738.easyarmorstands.group.tool;

import me.m56738.easyarmorstands.api.editor.Snapper;
import me.m56738.easyarmorstands.api.editor.tool.MoveTool;
import me.m56738.easyarmorstands.api.editor.tool.MoveToolSession;
import me.m56738.easyarmorstands.api.util.PositionProvider;
import me.m56738.easyarmorstands.api.util.RotationProvider;
import me.m56738.easyarmorstands.message.Message;
import me.m56738.easyarmorstands.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.List;

public class GroupMoveTool implements MoveTool {
    private final PositionProvider positionProvider;
    private final RotationProvider rotationProvider;
    private final List<MoveTool> tools;

    public GroupMoveTool(PositionProvider positionProvider, RotationProvider rotationProvider, List<MoveTool> tools) {
        this.positionProvider = positionProvider;
        this.rotationProvider = rotationProvider;
        this.tools = new ArrayList<>(tools);
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
        return new SessionImpl();
    }

    @Override
    public boolean canUse(@NotNull Player player) {
        for (MoveTool tool : tools) {
            if (tool.canUse(player)) {
                return true;
            }
        }
        return false;
    }

    private class SessionImpl extends GroupToolSession<MoveToolSession> implements MoveToolSession {
        private final Vector3dc originalPosition;
        private final Vector3d offset = new Vector3d();

        private SessionImpl() {
            super(tools);
            originalPosition = new Vector3d(positionProvider.getPosition());
        }

        @Override
        public void setChange(@NotNull Vector3dc change) {
            this.offset.set(change);
            for (MoveToolSession session : sessions) {
                session.setChange(change);
            }
        }

        @Override
        public void snapChange(@NotNull Vector3d change, @NotNull Snapper context) {
            context.snapOffset(change);
        }

        @Override
        public @NotNull Vector3dc getPosition() {
            return originalPosition.add(offset, new Vector3d());
        }

        @Override
        public void setPosition(@NotNull Vector3dc position) {
            position.sub(originalPosition, offset);
            setChange(offset);
        }

        @Override
        public @Nullable Component getStatus() {
            return Util.formatOffset(offset);
        }

        @Override
        public @Nullable Component getDescription() {
            Component count = Component.text(sessions.size());
            Component value = Util.formatOffset(offset);
            return Message.component("easyarmorstands.history.group.move", count, value);
        }
    }
}
