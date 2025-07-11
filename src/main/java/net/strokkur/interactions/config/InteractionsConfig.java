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
