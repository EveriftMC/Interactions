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
