package net.strokkur.interactions.config;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.strokkur.interactions.InteractionsPlugin;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InteractionsConfigImpl implements InteractionsConfig {

    @MonotonicNonNull
    private ConfigModel configModel = null;

    @MonotonicNonNull
    private List<ActionItem> actionItems = null;

    @Override
    public void reload(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            InteractionsPlugin.getPlugin(InteractionsPlugin.class).saveResource(FILE_NAME, false);
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .path(path)
            .indent(2)
            .prettyPrinting(true)
            .build();

        CommentedConfigurationNode node = loader.load();
        ConfigModel configModel = node.get(ConfigModel.class);

        Preconditions.checkState(configModel != null, "The configuration failed to load. (" + path + ")");

        this.configModel = configModel;
        this.actionItems = configModel.actions.stream()
            .map(def -> (ActionItem) new ActionItemImpl(def.slot, def.command, def.item))
            .toList();
    }

    @Override
    public Component getInventoryTitle(TagResolver... resolvers) {
        Preconditions.checkState(configModel != null, "The configuration is not loaded.");
        return MiniMessage.miniMessage().deserialize(configModel.inventoryTitle, resolvers);
    }

    @Override
    public ItemStack getBackgroundItem() {
        Preconditions.checkState(configModel != null, "The configuration is not loaded.");
        return configModel.backgroundItem.create();
    }

    @Override
    public List<ActionItem> getActionItems() {
        Preconditions.checkState(actionItems != null, "The configuration is not loaded.");
        return actionItems;
    }

    private record ActionItemImpl(int slot, @Nullable String command, ConfigModel.ItemDefinition itemDefinition) implements ActionItem {

        @Override
        public @Nullable String getCommand(TagResolver... resolvers) {
            if (command == null) {
                return null;
            }

            // This is super ultra scuffed
            String plainSerialized = MiniMessage.miniMessage().serialize(Component.text(command));
            Component serialized = MiniMessage.miniMessage().deserialize(plainSerialized, resolvers);
            return PlainTextComponentSerializer.plainText().serialize(serialized);
        }

        @Override
        public ItemStack getItem(TagResolver... resolvers) {
            return itemDefinition.create(resolvers);
        }
    }
}
