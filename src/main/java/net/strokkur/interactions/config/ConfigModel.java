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
package net.strokkur.interactions.config;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
class ConfigModel {

    public String inventoryTitle = "<gradient:#87f9ff:#87f9ff><b>Interaction menu for </gradient><target>";

    public ItemDefinition backgroundItem = new ItemDefinition();

    public List<ActionDefinition> actions = List.of();

    @SuppressWarnings("UnstableApiUsage")
    @ConfigSerializable
    static class ItemDefinition {
        public @Nullable String type = null;
        public @Nullable String name = null;
        public List<String> lore = List.of();

        public boolean hideTooltip = false;
        public boolean enchanted = false;
        public int amount = 1;

        public ItemStack create(TagResolver... resolvers) {
            if (type == null) {
                return ItemStack.empty();
            }

            Key key = Key.key(type.toLowerCase());
            ItemType type = Registry.ITEM.getOrThrow(key);

            int maxAmount = Math.clamp(amount, 1, 99);

            ItemStack stack = type.createItemStack();
            stack.setData(DataComponentTypes.MAX_STACK_SIZE, maxAmount);
            stack.setAmount(maxAmount);

            if (hideTooltip) {
                stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                    .hideTooltip(true)
                    .build());
            }

            if (enchanted) {
                stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            if (name != null) {
                stack.setData(DataComponentTypes.ITEM_NAME, MiniMessage.miniMessage().deserialize(name, resolvers));
            }

            if (!lore.isEmpty()) {
                stack.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .lines(lore.stream()
                        .map(line -> MiniMessage.miniMessage().deserialize(line, resolvers))
                        .map(line -> line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .toList())
                    .build());
            }

            return stack;
        }
    }

    @ConfigSerializable
    static class ActionDefinition {
        public int slot = 1;
        public String command = "";
        public ItemDefinition item = new ItemDefinition();
    }
}
