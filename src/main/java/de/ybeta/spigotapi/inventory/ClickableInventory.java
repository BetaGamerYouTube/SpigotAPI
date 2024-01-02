package de.ybeta.spigotapi.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.ybeta.spigotapi.SpigotPlugin;
import de.ybeta.spigotapi.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class ClickableInventory implements Listener {
    public static final int[] ROW_SIZES = new int[]{9, 18, 27, 36, 45, 54};
    public static ItemStack BLACK_GLASS_BORDER;
    public static ItemStack SILVER_GLASS_BORDER;
    public static final String[] CENTER_SPACE_PATTERN;
    public static final String[] CENTER_ALIGNED_PATTERN;
    private ClickableItem[] items;
    private final Collection<Player> viewers = Lists.newArrayList();
    private Inventory inventory;
    private boolean destroyable = true;
    private boolean editable = false;
    private CloseListener closeListener;
    private final Server server;

    public ClickableInventory(String title, int rows) {
        SpigotPlugin plugin = SpigotPlugin.getInstance();
        this.server = plugin.getServer();
        this.inventory = this.server.createInventory((InventoryHolder)null, rows * 9, title);
        this.server.getPluginManager().registerEvents(this, plugin);
        this.items = new ClickableItem[this.getSize()];
    }

    public void setItem(int slot, ClickableItem item) {
        this.setItem(slot, (ClickableItem)item, (ClickableItem.ClickListener)null);
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.setItem(slot, ClickableItem.create(itemStack));
    }

    public void setItem(int from, int to, ItemStack itemStack) {
        ClickableItem item = ClickableItem.create(itemStack);

        for(int slot = from; slot < to; ++slot) {
            this.setItem(slot, item);
        }

    }

    public void setItem(int from, int to, ClickableItem item) {
        for(int slot = from; slot < to; ++slot) {
            this.setItem(slot, item);
        }

    }

    public void setDestroyable(boolean destroyable) {
        this.destroyable = destroyable;
    }

    public void setItem(int slot, ItemStack item, ClickableItem.ClickListener click) {
        this.setItem(slot, ClickableItem.create(item), click);
    }

    public void setItem(int slot, ClickableItem item, ClickableItem.ClickListener click) {
        Preconditions.checkArgument(slot < this.inventory.getSize(), "Slot is too high");
        Preconditions.checkArgument(slot >= 0, "Slot is too low");
        this.items[slot] = item.setClickListener(click).setSlot(slot);
        this.inventory.setItem(slot, item.getItemStack());
    }

    public Collection<Player> getViewers() {
        return this.viewers;
    }

    public void open() {
        Preconditions.checkNotNull(this.inventory, "Inventory has to be defined!");
        Preconditions.checkState(!this.viewers.isEmpty(), "Viewers has to be defined!");
        Iterator var1 = this.viewers.iterator();

        while(var1.hasNext()) {
            Player viewer = (Player)var1.next();
            viewer.openInventory(this.inventory);
        }

    }

    public void open(Player viewer) {
        Preconditions.checkNotNull(this.inventory, "Inventory has to be defined!");
        viewer.openInventory(this.inventory);
        this.addViewer(viewer);
    }

    public void open(Iterable<Player> viewers) {
        Preconditions.checkNotNull(this.inventory, "Inventory has to be defined!");
        Iterator var2 = viewers.iterator();

        while(var2.hasNext()) {
            Player viewer = (Player)var2.next();
            viewer.openInventory(this.inventory);
            this.addViewer(viewer);
        }

    }

    public void addViewer(Player viewer) {
        synchronized(this.viewers) {
            this.viewers.add(viewer);
        }
    }

    public void refresh() {
        synchronized(this.viewers) {
            Iterator var2 = this.viewers.iterator();

            while(var2.hasNext()) {
                Player player = (Player)var2.next();
                player.updateInventory();
            }

        }
    }

    public void close() {
        if (this.destroyable) {
            synchronized(this.viewers) {
                this.viewers.clear();
            }

            HandlerList.unregisterAll(this);
        }

        if (this.closeListener != null) {
            this.closeListener.close();
        }

    }

    public void close(Player viewer) {
        synchronized(this.viewers) {
            this.viewers.remove(viewer);
        }

        if (this.viewers.isEmpty()) {
            this.close();
        }

    }

    public void fill(ItemStack itemStack) {
        ClickableItem item = ClickableItem.create(itemStack);

        for(int slot = 0; slot < this.inventory.getSize(); ++slot) {
            this.setItem(slot, item);
        }

    }

    public void setBorder(ItemStack itemStack) {
        ClickableItem clickableItem = ClickableItem.create(itemStack);

        for(int slot = 0; slot < this.inventory.getSize(); ++slot) {
            if (slot <= 8 || slot % 9 == 0 || slot >= this.inventory.getSize() - 8 || slot % 8 == slot / 9) {
                this.setItem(slot, clickableItem);
            }
        }

    }

    public void setBorderFilled(ItemStack borderStack, ItemStack fillingStack) {
        ClickableItem border = ClickableItem.create(borderStack);
        ClickableItem filling = ClickableItem.create(fillingStack);

        for(int slot = 0; slot < this.inventory.getSize(); ++slot) {
            if (slot > 8 && slot % 9 != 0 && slot < this.inventory.getSize() - 8 && slot % 8 != slot / 9) {
                this.setItem(slot, filling);
            } else {
                this.setItem(slot, border);
            }
        }

    }

    public void formatAllCentered() {
        this.formatAll(CENTER_SPACE_PATTERN);
    }

    public void formatAll(String[] pattern) {
        for(int row = 0; row < this.getRows(); ++row) {
            this.formatItems(row, pattern);
        }

    }

    public ClickableInventory formatCenterSpaced(int row) {
        return this.formatItems(row, CENTER_SPACE_PATTERN);
    }

    public ClickableInventory formatItems(int row, String[] pattern) {
        List<ClickableItem> items = Lists.newArrayListWithExpectedSize(9);
        int slots = 0;

        for(int slot = row * 9; slot < Math.min(this.inventory.getSize(), row * 9 + 9); ++slot) {
            ClickableItem left = this.items[slot];
            if (left != null) {
                items.add(left);
                ++slots;
            }
        }

        Iterator<ClickableItem> iterator = items.iterator();
        String patternLine = pattern[slots];

        for(int slot = 0; slot < 9; ++slot) {
            if (patternLine.charAt(slot) == 'X') {
                ClickableItem next = (ClickableItem)iterator.next();
                this.setItem(row * 9 + slot, next, next.getClickListener());
            } else {
                this.remove(row * 9 + slot);
            }
        }

        return this;
    }

    public void clear() {
        Arrays.fill(this.items, (Object)null);
        this.inventory.clear();
    }

    public void remove(int slot) {
        this.inventory.setItem(slot, (ItemStack)null);
        this.items[slot] = null;
    }

    public void clear(int beginIndex) {
        this.clear(beginIndex, this.inventory.getSize());
    }

    public void clear(int beginIndex, int endIndex) {
        for(int index = beginIndex; index < endIndex; ++index) {
            this.inventory.setItem(index, (ItemStack)null);
            this.items[index] = null;
        }

    }

    public void clearExcluded(int beginIndex, int endIndex, Material exclusion) {
        for(int index = beginIndex; index < endIndex; ++index) {
            ItemStack slotItem = this.inventory.getItem(index);
            if (slotItem == null || slotItem.getType() != exclusion) {
                this.inventory.setItem(index, (ItemStack)null);
                this.items[index] = null;
            }
        }

    }

    public int getSize() {
        return this.inventory.getSize();
    }

    public int getRows() {
        return this.getSize() / 9;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Nullable
    public ClickableItem getItem(int slot) {
        return this.items[slot];
    }

    public void setEditable() {
        this.editable = true;
    }

    public void setUnmodifiable() {
        this.editable = false;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @EventHandler
    public void click(InventoryClickEvent click) {
        if (click.getWhoClicked() instanceof Player && click.getClickedInventory() != null && click.getClickedInventory().equals(this.inventory)) {
            ItemStack clickedItem = click.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                ClickableItem item = this.items[click.getSlot()];
                if (item == null) {
                    if (!this.editable) {
                        click.setCancelled(true);
                    }
                } else {
                    if (!this.editable && !item.isMovable()) {
                        click.setCancelled(true);
                    }

                    ClickableItem.ClickListener clickListener = item.getClickListener();
                    if (clickListener != null) {
                        clickListener.click(click, clickedItem);
                    }

                }
            }
        } else if (click.getWhoClicked() instanceof Player && viewers.contains((Player) click.getWhoClicked())) {
            click.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (this.viewers.contains(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void close(InventoryCloseEvent closure) {
        HumanEntity entity = closure.getPlayer();
        if (entity instanceof Player && closure.getInventory().equals(this.inventory)) {
            this.close((Player)entity);
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void close(PlayerQuitEvent quit) {
        this.close(quit.getPlayer());
    }

    public ClickableInventoryBuilder toBuilder() {
        return ClickableInventory.ClickableInventoryBuilder.of(this);
    }

    static {
        BLACK_GLASS_BORDER = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§a").build();
        SILVER_GLASS_BORDER = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName("§a").build();
        CENTER_SPACE_PATTERN = new String[]{"?????????", "????X????", "???X?X???", "??X?X?X??", "?X?X?X?X?", "X?X?X?X?X", "XX?X?X?XX", "?XXXXXXX?", "XXXX?XXXX", "XXXXXXXXX"};
        CENTER_ALIGNED_PATTERN = new String[]{"?????????", "????X????", "???XX????", "???XXX???", "??XXXX???", "??XXXXX??", "?XXXXXX??", "?XXXXXXX?", "XXXXXXXX?", "XXXXXXXXX"};
    }

    public interface CloseListener {
        void close();
    }

    public static final class ClickableInventoryBuilder {
        private final ClickableInventory prototype;
        private String title;
        private int rows = -1;

        private ClickableInventoryBuilder(ClickableInventory prototype) {
            this.prototype = prototype;
        }

        public static ClickableInventoryBuilder of(ClickableInventory prototype) {
            return new ClickableInventoryBuilder(prototype);
        }

        public ClickableInventoryBuilder withTitle(String title) {
            if (this.rows != -1 && this.prototype.inventory == null) {
                Preconditions.checkArgument(this.rows < 7, "Inventory is too big");
                Preconditions.checkArgument(this.rows > 0, "Inventory is too small");
                this.prototype.inventory = this.prototype.server.createInventory((InventoryHolder)null, this.rows * 9, title);
                this.prototype.items = new ClickableItem[this.prototype.getSize()];
            }

            this.title = title;
            return this;
        }

        public ClickableInventoryBuilder withRows(int rows) {
            Preconditions.checkArgument(rows < 7, "Inventory is too big");
            Preconditions.checkArgument(rows > 0, "Inventory is too small");
            if (this.title != null && !this.title.isEmpty() && this.prototype.inventory == null) {
                this.prototype.inventory = this.prototype.server.createInventory((InventoryHolder)null, rows * 9, this.title);
                this.prototype.items = new ClickableItem[this.prototype.getSize()];
            }

            this.rows = rows;
            return this;
        }

        public ClickableInventoryBuilder withViewer(Player viewer) {
            synchronized(this.prototype.viewers) {
                this.prototype.viewers.add(viewer);
                return this;
            }
        }

        public ClickableInventory build() {
            return this.prototype;
        }
    }
}

