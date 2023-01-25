package gg.bundlegroup.easyarmorstands.core.inventory;

import gg.bundlegroup.easyarmorstands.core.platform.EasArmorEntity;
import gg.bundlegroup.easyarmorstands.core.platform.EasInventory;
import gg.bundlegroup.easyarmorstands.core.platform.EasItem;
import gg.bundlegroup.easyarmorstands.core.session.Session;

public class EquipmentItemSlot implements InventorySlot {
    private final SessionMenu menu;
    private final Session session;
    private final EasInventory inventory;
    private final EasArmorEntity.Slot slot;

    public EquipmentItemSlot(SessionMenu inventory, EasArmorEntity.Slot slot) {
        this.menu = inventory;
        this.session = inventory.getSession();
        this.inventory = inventory.getInventory();
        this.slot = slot;
    }

    @Override
    public void initialize(int slot) {
        inventory.setItem(slot, session.getEntity().getItem(this.slot));
    }

    @Override
    public boolean onInteract(int slot, boolean click, boolean put, boolean take, EasItem cursor) {
        menu.queueTask(() -> session.getEntity().setItem(this.slot, inventory.getItem(slot)));
        return true;
    }
}