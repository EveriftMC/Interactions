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

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.strokkur.interactions.config.InteractionsConfig;
import net.strokkur.interactions.fastinv.FastInvManager;
import net.strokkur.interactions.gui.InteractionsMenu;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;

@NullMarked
public class InteractionsPlugin extends JavaPlugin {
    
    @MonotonicNonNull
    private Injector injector = null;

    @Override
    public void onLoad() {
        injector = Guice.createInjector(new InteractionsModule());
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
    
    public static InteractionsPlugin getInstance() {
        return InteractionsPlugin.getPlugin(InteractionsPlugin.class);
    }

    public static Logger logger() {
        return getInstance().getSLF4JLogger();
    }

    public static Path dataPath() {
        return getInstance().getDataPath();
    }

    public static InteractionsMenu getInteractionsMenu() {
        return getInstance().injector.getInstance(InteractionsMenu.class);
    }

    public static InteractionsConfig getInteractionsConfig() {
        return getInstance().injector.getInstance(InteractionsConfig.class);
    }
}
