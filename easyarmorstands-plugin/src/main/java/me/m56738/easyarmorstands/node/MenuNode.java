package me.m56738.easyarmorstands.node;

import me.m56738.easyarmorstands.api.editor.EyeRay;
import me.m56738.easyarmorstands.api.editor.Session;
import me.m56738.easyarmorstands.api.editor.button.Button;
import me.m56738.easyarmorstands.api.editor.button.ButtonResult;
import me.m56738.easyarmorstands.api.editor.context.ClickContext;
import me.m56738.easyarmorstands.api.editor.context.EnterContext;
import me.m56738.easyarmorstands.api.editor.context.ExitContext;
import me.m56738.easyarmorstands.api.editor.context.UpdateContext;
import me.m56738.easyarmorstands.api.editor.node.Node;
import me.m56738.easyarmorstands.api.editor.node.menu.MenuButton;
import net.kyori.adventure.text.Component;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A node which can contain multiple {@link Button buttons}.
 */
public abstract class MenuNode implements Node {
    private final Session session;
    private final Map<MenuButton, Button> buttons = new HashMap<>();
    private MenuButton targetButton;
    private Vector3dc targetCursor;
    private boolean visible;

    public MenuNode(Session session) {
        this.session = session;
    }

    public void addButton(MenuButton menuButton) {
        setButton(menuButton, menuButton.getButton());
    }

    public void removeButton(MenuButton menuButton) {
        setButton(menuButton, null);
    }

    private void setButton(MenuButton menuButton, Button button) {
        Button oldButton;
        if (button != null) {
            oldButton = buttons.put(menuButton, button);
        } else {
            oldButton = buttons.remove(menuButton);
        }
        if (visible) {
            if (oldButton != null) {
                oldButton.hidePreview();
            }
            if (button != null) {
                button.update();
                button.updatePreview(false);
                button.showPreview();
            }
        }
    }

    @Override
    public void onEnter(EnterContext context) {
        targetButton = null;
        visible = true;
        for (Button button : buttons.values()) {
            button.update();
            button.updatePreview(false);
            button.showPreview();
        }
    }

    @Override
    public void onExit(ExitContext context) {
        targetButton = null;
        visible = false;
        for (Button button : buttons.values()) {
            button.hidePreview();
        }
    }

    @Override
    public void onUpdate(UpdateContext context) {
        EyeRay ray = context.eyeRay();
        Button bestButton = null;
        MenuButton bestMenuButton = null;
        Vector3dc bestCursor = null;
        int bestPriority = Integer.MIN_VALUE;
        double bestDistance = Double.POSITIVE_INFINITY;
        List<ButtonResult> results = new ArrayList<>();
        for (Map.Entry<MenuButton, Button> entry : buttons.entrySet()) {
            MenuButton menuButton = entry.getKey();
            Button button = entry.getValue();
            button.update();
            button.intersect(ray, results::add);
            for (ButtonResult result : results) {
                Vector3dc position = result.position();
                int priority = result.priority();
                if (priority < bestPriority) {
                    continue;
                }
                double distance = position.distanceSquared(ray.origin());
                if (priority > bestPriority || distance < bestDistance) {
                    bestButton = button;
                    bestMenuButton = menuButton;
                    bestCursor = position;
                    bestPriority = priority;
                    bestDistance = distance;
                }
            }
            results.clear();
        }
        for (Button button : buttons.values()) {
            button.updatePreview(button == bestButton);
        }
        Component targetName;
        if (bestButton != null) {
            targetName = bestMenuButton.getName();
        } else {
            targetName = Component.empty();
        }
        session.setSubtitle(targetName);
        targetButton = bestMenuButton;
        targetCursor = bestCursor;
    }

    @Override
    public boolean onClick(ClickContext context) {
        if (context.type() == ClickContext.Type.RIGHT_CLICK) {
            if (targetButton != null) {
                targetButton.onClick(session, targetCursor);
                return true;
            }
        }
        return false;
    }
}
