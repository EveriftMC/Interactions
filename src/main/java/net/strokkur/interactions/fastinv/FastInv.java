/*
 * Interactions - Simple /interact plugin.
 * Copyright (C) 2025  Strokkur24
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.strokkur.interactions.fastinv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky
 * @version 3.1.2
 */
public class FastInv implements InventoryHolder {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();

    private final Inventory inventory;

    private Predicate<Player> closeFilter;

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size a multiple of 9 as the size of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int)
     */
    public FastInv(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  a multiple of 9 as the size of the inventory
     * @param title the title (name) of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int, String)
     */
    public FastInv(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type the type of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType)
     */
    public FastInv(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  the type of the inventory
     * @param title the title of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType, String)
     */
    public FastInv(InventoryType type, String title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public FastInv(Function<FastInv, Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    /**
     * Called when the inventory is opened.
     *
     * @param event the InventoryOpenEvent that triggered this method
     */
    protected void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Called when the inventory is clicked.
     *
     * @param event the InventoryClickEvent that triggered this method
     */
    protected void onClick(InventoryClickEvent event) {
    }

    /**
     * Called when the player drags an item in their cursor across the inventory.
     *
     * @param event the InventoryDragEvent that triggered this method
     */
    protected void onDrag(InventoryDragEvent event) {
    }

    /**
     * Called when the inventory is closed.
     *
     * @param event the InventoryCloseEvent that triggered this method
     */
    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot, with no click handler.
     *
     * @param item the item to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    the item to add.
     * @param handler the click handler associated with this item
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot, with no click handler.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    the slot where to add the item
     * @param item    the item to add.
     * @param handler the click handler associated with this item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots, with no click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     The item to add.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     the item to add
     * @param handler  the click handler associated with these items
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i < slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the slots where to add the item
     * @param item  the item to add
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots with a click handler.
     *
     * @param slots   the slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated with this item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the list of slots where to add the item
     * @param item  the item to add
     */
    public void setItems(Iterable<Integer> slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots with a click handler.
     *
     * @param slots   the list of slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated with this item
     */
    public void setItems(Iterable<Integer> slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (Integer slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot the slot from where to remove the item
     */
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Remove multiples {@link ItemStack} from the inventory.
     *
     * @param slots the slots from where to remove the items
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Clear all items from the inventory and remove the click handlers.
     */
    public void clearItems() {
        this.inventory.clear();
        this.itemHandlers.clear();
    }

    /**
     * Add a close filter to prevent players from closing the inventory.
     * To prevent a player from closing the inventory the predicate should return {@code true}.
     *
     * @param closeFilter The close filter
     */
    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    /**
     * Add a handler that will be called when the inventory is opened.
     *
     * @param openHandler the handler to add
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler that will be called when the inventory is closed.
     *
     * @param closeHandler the handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler that will be called when an item is clicked.
     *
     * @param clickHandler the handler to add
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Add a handler that will be called when the player drags an item in their cursor across the inventory.
     *
     * @param dragHandler the handler to add
     */
    public void addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        this.dragHandlers.add(dragHandler);
    }

    /**
     * Open the inventory to the given player.
     *
     * @param player the player to open the inventory to
     */
    public void open(Player player) {
        Objects.requireNonNull(player, "player").openInventory(this.inventory);
    }

    /**
     * Get the borders of this inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return the inventory borders slots
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                                                    || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get the corners of this inventory.
     *
     * @return the inventory corners slots
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                                                    || i == 17 || i == size - 18
                                                    || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     * Get the underlying Bukkit inventory.
     *
     * @return the Bukkit inventory
     */
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

    void handleDrag(InventoryDragEvent e) {
        onDrag(e);

        this.dragHandlers.forEach(c -> c.accept(e));
    }
}