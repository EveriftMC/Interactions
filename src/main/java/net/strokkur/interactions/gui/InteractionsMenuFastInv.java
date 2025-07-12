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
package net.strokkur.interactions.gui;

import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.strokkur.interactions.config.InteractionsConfig;
import net.strokkur.interactions.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InteractionsMenuFastInv implements InteractionsMenu {

    private final InteractionsConfig config;

    @Inject
    public InteractionsMenuFastInv(InteractionsConfig config) {
        this.config = config;
    }

    @Override
    public void open(Player player, Player target) {
        TagResolver resolvers = TagResolver.resolver(
            TagResolver.resolver("player", Tag.preProcessParsed(player.getName())),
            TagResolver.resolver("target", Tag.preProcessParsed(target.getName()))
        );

        FastInv inv = new FastInv(i -> Bukkit.createInventory(i, 27, config.getInventoryTitle(resolvers)));

        inv.setItems(0, 27, config.getBackgroundItem());

        for (InteractionsConfig.ActionItem action : config.getActionItems()) {
            inv.setItem(action.slot() - 1, action.getItem(resolvers), event -> {
                String command = action.getCommand(resolvers);
                if (command != null) {
                    player.performCommand(command);
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            });
        }

        inv.open(player);
        player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1f, 1f);
    }
}
