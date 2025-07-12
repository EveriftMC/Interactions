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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.strokkur.interactions.abstraction.impl.BukkitPlayerWrapper;
import net.strokkur.interactions.config.InteractionsConfig;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class InteractionsPluginBootstrap implements PluginBootstrap {

    private static LiteralCommandNode<CommandSourceStack> getMainCommand() {
        return Commands.literal("interact")
            .requires(stack -> stack.getSender().hasPermission("interact.command.use") && stack.getExecutor() instanceof Player)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes(ctx -> {
                    if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                        return 0;
                    }

                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class)
                        .resolve(ctx.getSource())
                        .stream()
                        .findFirst()
                        .orElseThrow();

                    InteractionsPlugin.getInstance().getInteractionsMenu().open(
                        new BukkitPlayerWrapper(player),
                        new BukkitPlayerWrapper(target)
                    );
                    return Command.SINGLE_SUCCESS;
                })
            )
            .build();
    }

    private static LiteralCommandNode<CommandSourceStack> getReloadCommand() {
        return Commands.literal("interactreload")
            .requires(stack -> stack.getSender().hasPermission("interact.command.reload"))
            .executes(ctx -> {
                try {
                    InteractionsPlugin.getInstance().getInteractionsConfig().reload(
                        InteractionsPlugin.dataPath().resolve(InteractionsConfig.FILE_NAME)
                    );
                } catch (Exception exception) {
                    ctx.getSource().getSender().sendRichMessage(
                        "<red>A fatal exception occurred whilst reloading '" + InteractionsConfig.FILE_NAME + "'. See the console for more information."
                    );
                    InteractionsPlugin.logger().error("An exception occurred whilst reloading '" + InteractionsConfig.FILE_NAME + "'", exception);
                    return 0;
                }

                ctx.getSource().getSender().sendRichMessage("<green>Successfully reloaded '" + InteractionsConfig.FILE_NAME + "'!");
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(
            event -> {
                Commands commands = event.registrar();
                commands.register(getMainCommand(), "Opens an interaction menu for a player.");
                commands.register(getReloadCommand(), "Reload the plugin's main configuration.");
            }
        ));
    }
}
