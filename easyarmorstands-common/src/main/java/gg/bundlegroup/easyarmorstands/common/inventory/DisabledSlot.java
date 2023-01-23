package gg.bundlegroup.easyarmorstands.common.inventory;

import gg.bundlegroup.easyarmorstands.common.platform.EasInventory;
import gg.bundlegroup.easyarmorstands.common.platform.EasItem;

public class DisabledSlot implements InventorySlot {
    private final EasInventory inventory;
    private final EasItem item;

    public DisabledSlot(EasInventory inventory, EasItem item) {
        this.inventory = inventory;
        this.item = item;
    }

    @Override
    public void initialize(int slot) {
        inventory.setItem(slot, item);
    }

    @Override
    public boolean onInteract(int slot, boolean click, boolean put, boolean take, EasItem cursor) {
        return false;
    }
}
