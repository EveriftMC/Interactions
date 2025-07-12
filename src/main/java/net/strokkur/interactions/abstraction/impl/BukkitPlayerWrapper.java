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
package net.strokkur.interactions.abstraction.impl;

import net.strokkur.interactions.abstraction.CommandPerformable;
import net.strokkur.interactions.abstraction.InventoryClosable;
import net.strokkur.interactions.abstraction.Named;
import net.strokkur.interactions.abstraction.PlayerGettable;
import net.strokkur.interactions.abstraction.SoundPlayable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BukkitPlayerWrapper implements InventoryClosable, Named, PlayerGettable, SoundPlayable, CommandPerformable {

    private final Player player;

    public BukkitPlayerWrapper(Player player) {
        this.player = player;
    }

    @Override
    public void performCommand(String command) {
        player.performCommand(command);
    }

    @Override
    public void closeInventory(InventoryCloseEvent.Reason reason) {
        player.closeInventory(reason);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void playSound(Sound sound) {
        player.playSound(player, sound, 1f, 1f);
    }
}
