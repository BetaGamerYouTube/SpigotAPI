package de.ybeta.spigotapi.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class ClickableItem {
    private ClickListener clickListener;
    private ItemStack itemStack;
    private boolean movable = false;
    private int slot;

    private ClickableItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ClickableItem setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public boolean hasClickListener() {
        return this.clickListener != null;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public ClickableItem movable() {
        this.movable = true;
        return this;
    }

    public ClickableItem immovable() {
        this.movable = false;
        return this;
    }

    public ClickableItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ClickableItem create(ItemStack itemStack) {
        return new ClickableItem(itemStack);
    }

    public interface ClickListener {
        void click(InventoryClickEvent var1, ItemStack var2);
    }
}

