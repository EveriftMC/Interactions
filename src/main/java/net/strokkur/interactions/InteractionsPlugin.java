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
package net.strokkur.interactions;

import net.strokkur.interactions.abstraction.config.ActionItemable;
import net.strokkur.interactions.abstraction.config.BackgroundItemable;
import net.strokkur.interactions.abstraction.config.InventoryTitleable;
import net.strokkur.interactions.abstraction.config.Reloadable;
import net.strokkur.interactions.config.InteractionsConfig;
import net.strokkur.interactions.config.InteractionsConfigImpl;
import net.strokkur.interactions.fastinv.FastInvManager;
import net.strokkur.interactions.gui.InteractionsMenuFastInv;
import net.strokkur.interactions.gui.TargetOpenable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;

@NullMarked
public class InteractionsPlugin extends JavaPlugin {

    private final TargetOpenable openable;
    private final InteractionsConfig config;

    public InteractionsPlugin() {
        this.config = new InteractionsConfigImpl();
        this.openable = new InteractionsMenuFastInv(config);
    }

    public static InteractionsPlugin getInstance() {
        return InteractionsPlugin.getPlugin(InteractionsPlugin.class);
    }

    public static Logger logger() {
        return getInstance().getSLF4JLogger();
    }

    public static Path dataPath() {
        return getInstance().getDataPath();
    }

    @Override
    public void onEnable() {
        FastInvManager.register(this);

        try {
            getInteractionsConfig().reload(getDataPath().resolve(InteractionsConfig.FILE_NAME));
        } catch (Exception exception) {
            InteractionsPlugin.logger().error("An exception occurred whilst reloading '" + InteractionsConfig.FILE_NAME + "'. Run '/interactionsreload' after you fix all issues!", exception);
        }
    }

    public TargetOpenable getInteractionsMenu() {
        return openable;
    }

    public <R extends InventoryTitleable & BackgroundItemable & ActionItemable & Reloadable> R getInteractionsConfig() {
        return (R) config;
    }
}
