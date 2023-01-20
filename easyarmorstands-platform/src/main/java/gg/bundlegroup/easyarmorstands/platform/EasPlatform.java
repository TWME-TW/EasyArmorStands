package gg.bundlegroup.easyarmorstands.platform;

import cloud.commandframework.CommandManager;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public interface EasPlatform {
    CommandManager<EasCommandSender> commandManager();

    boolean canHideEntities();

    boolean canSetEntityPersistence();

    boolean canSetEntityGlowing();

    boolean canSpawnParticles();

    boolean hasSlot(EasArmorEntity.Slot slot);

    Collection<? extends EasPlayer> getPlayers();

    EasInventory createInventory(Component title, int width, int height, EasInventoryListener listener);

    void registerListener(EasListener listener);

    void registerTickTask(Runnable task);
}
