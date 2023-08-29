package me.m56738.easyarmorstands.api.editor.node.menu;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface MenuButtonProvider {
    MoveButtonBuilder move();

    RotateButtonBuilder rotate();

    CarryButtonBuilder carry();
}
