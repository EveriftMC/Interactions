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

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.strokkur.interactions.abstraction.CommandPerformable;
import net.strokkur.interactions.abstraction.InventoryClosable;
import net.strokkur.interactions.abstraction.Named;
import net.strokkur.interactions.abstraction.PlayerGettable;
import net.strokkur.interactions.abstraction.SoundPlayable;
import net.strokkur.interactions.abstraction.config.ActionItemable;
import net.strokkur.interactions.abstraction.config.BackgroundItemable;
import net.strokkur.interactions.abstraction.config.InventoryTitleable;
import net.strokkur.interactions.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InteractionsMenuFastInv<C extends ActionItemable & BackgroundItemable & InventoryTitleable>
    implements TargetOpenable {

    private final C config;

    public InteractionsMenuFastInv(C config) {
        this.config = config;
    }

    @Override
    public <P extends CommandPerformable & PlayerGettable & InventoryClosable & SoundPlayable & Named, T extends Named>
    void open(P player, T target) {
        TagResolver resolvers = TagResolver.resolver(
            TagResolver.resolver("player", Tag.preProcessParsed(player.getName())),
            TagResolver.resolver("target", Tag.preProcessParsed(target.getName()))
        );

        FastInv inv = new FastInv(i -> Bukkit.createInventory(i, 27, config.getInventoryTitle(resolvers)));

        inv.setItems(0, 27, config.getBackgroundItem());

        for (var action : config.getActionItems()) {
            inv.setItem(action.getSlot() - 1, action.getItem(resolvers), event -> {
                String command = action.getCommand(resolvers);
                if (command != null) {
                    player.performCommand(command);
                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            });
        }

        inv.open(player.getPlayer());
        player.playSound(Sound.BLOCK_CHEST_OPEN);
    }
}
