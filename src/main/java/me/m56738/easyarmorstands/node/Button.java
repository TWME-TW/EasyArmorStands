package me.m56738.easyarmorstands.node;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public interface Button {
    void update(Vector3dc eyes, Vector3dc target);

    void showPreview(boolean focused);

    /**
     * Returns the location where the eye ray intersects this node.
     *
     * @return Position of the intersection with the eye ray, or null if there is none.
     */
    @Nullable Vector3dc getLookTarget();

    default int getPriority() {
        return 0;
    }

    Component getName();

    /**
     * Creates a node that should be entered when this button is clicked.
     *
     * @return The node which can be entered using this button.
     */
    Node createNode();
}
