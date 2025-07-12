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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface InteractionsConfig {
    
    String FILE_NAME = "config.conf";
    
    void reload(Path path) throws IOException;
    

    Component getInventoryTitle(TagResolver... resolvers);

    ItemStack getBackgroundItem();
    
    List<ActionItem> getActionItems();

    interface ActionItem {

        int slot();

        @Nullable
        String getCommand(TagResolver... resolvers);

        ItemStack getItem(TagResolver... resolvers);
    }
}
