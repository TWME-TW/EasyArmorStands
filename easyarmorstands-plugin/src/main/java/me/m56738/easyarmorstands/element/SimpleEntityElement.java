package me.m56738.easyarmorstands.element;

import me.m56738.easyarmorstands.EasyArmorStands;
import me.m56738.easyarmorstands.api.editor.Session;
import me.m56738.easyarmorstands.api.editor.button.Button;
import me.m56738.easyarmorstands.api.editor.node.Node;
import me.m56738.easyarmorstands.api.element.ConfigurableEntityElement;
import me.m56738.easyarmorstands.api.element.DestroyableElement;
import me.m56738.easyarmorstands.api.element.EntityElementReference;
import me.m56738.easyarmorstands.api.element.MenuElement;
import me.m56738.easyarmorstands.api.element.SelectableElement;
import me.m56738.easyarmorstands.api.property.PropertyRegistry;
import me.m56738.easyarmorstands.message.Message;
import me.m56738.easyarmorstands.node.SimpleEntityButton;
import me.m56738.easyarmorstands.node.SimpleEntityNode;
import me.m56738.easyarmorstands.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleEntityElement<E extends Entity> implements ConfigurableEntityElement<E>, SelectableElement, MenuElement, DestroyableElement {
    private final E entity;
    private final SimpleEntityElementType<E> type;
    private final PropertyRegistry properties = new Properties();

    public SimpleEntityElement(E entity, SimpleEntityElementType<E> type) {
        this.entity = entity;
        this.type = type;
    }

    @Override
    public E getEntity() {
        return entity;
    }

    @Override
    public @NotNull SimpleEntityElementType<E> getType() {
        return type;
    }

    @Override
    public @NotNull EntityElementReference<E> getReference() {
        return new EntityElementReferenceImpl<>(type, entity.getUniqueId());
    }

    @Override
    public boolean isValid() {
        return entity.isValid();
    }

    @Override
    public @NotNull PropertyRegistry getProperties() {
        return properties;
    }

    @Override
    public Button createButton(Session session) {
        return new SimpleEntityButton(session, entity);
    }

    @Override
    public Node createNode(Session session) {
        return new SimpleEntityNode(session, Message.component("easyarmorstands.node.select-axis"), this);
    }

    @Override
    public void openMenu(Player player) {
        Session session = EasyArmorStands.getInstance().getSessionManager().getSession(player);
        EasyArmorStands.getInstance().openEntityMenu(player, session, this);
    }

    @Override
    public void destroy() {
        entity.remove();
    }

    @Override
    public @NotNull Component getName() {
        return Component.text(Util.getId(entity.getUniqueId()));
    }

    private class Properties extends PropertyRegistry {
        @Override
        public boolean isValid() {
            return entity.isValid();
        }
    }
}
