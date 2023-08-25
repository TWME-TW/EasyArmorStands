package me.m56738.easyarmorstands.display;

import cloud.commandframework.brigadier.CloudBrigadierManager;
import io.leangen.geantyref.TypeToken;
import me.m56738.easyarmorstands.EasyArmorStands;
import me.m56738.easyarmorstands.api.element.EntityElementProviderRegistry;
import me.m56738.easyarmorstands.capability.entitytype.EntityTypeCapability;
import me.m56738.easyarmorstands.command.sender.EasCommandSender;
import me.m56738.easyarmorstands.display.command.BlockDataArgumentParser;
import me.m56738.easyarmorstands.display.command.DisplayCommands;
import me.m56738.easyarmorstands.display.editor.DisplaySessionListener;
import me.m56738.easyarmorstands.display.editor.node.DisplayRootNode;
import me.m56738.easyarmorstands.display.element.DisplayElementProvider;
import me.m56738.easyarmorstands.display.element.DisplayElementType;
import me.m56738.easyarmorstands.display.element.TextDisplayElementType;
import me.m56738.easyarmorstands.display.menu.DisplaySpawnSlotType;
import me.m56738.easyarmorstands.display.property.display.DefaultDisplayPropertyTypes;
import me.m56738.easyarmorstands.util.JOMLMapper;
import net.kyori.adventure.key.Key;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

import static me.m56738.easyarmorstands.api.menu.MenuSlotTypeRegistry.menuSlotTypeRegistry;

public class DisplayAddon {
    private final DisplayElementType<ItemDisplay> itemDisplayType;
    private final DisplayElementType<BlockDisplay> blockDisplayType;
    private final DisplayElementType<TextDisplay> textDisplayType;

    public DisplayAddon() {
        EasyArmorStands plugin = EasyArmorStands.getInstance();

        JOMLMapper mapper;
        try {
            mapper = new JOMLMapper();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        new DefaultDisplayPropertyTypes(plugin.getPropertyTypeRegistry());

        DisplaySessionListener listener = new DisplaySessionListener();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getServer().getPluginManager().registerEvents(new DisplayListener(mapper), plugin);

        itemDisplayType = new DisplayElementType<>(ItemDisplay.class, plugin.getCapability(EntityTypeCapability.class).getName(EntityType.ITEM_DISPLAY), DisplayRootNode::new);
        blockDisplayType = new DisplayElementType<>(BlockDisplay.class, plugin.getCapability(EntityTypeCapability.class).getName(EntityType.BLOCK_DISPLAY), DisplayRootNode::new);
        textDisplayType = new TextDisplayElementType(DisplayRootNode::new);

        menuSlotTypeRegistry().register(new DisplaySpawnSlotType(Key.key("easyarmorstands", "spawn/item_display"), itemDisplayType));
        menuSlotTypeRegistry().register(new DisplaySpawnSlotType(Key.key("easyarmorstands", "spawn/block_display"), blockDisplayType));
        menuSlotTypeRegistry().register(new DisplaySpawnSlotType(Key.key("easyarmorstands", "spawn/text_display"), textDisplayType));

        EntityElementProviderRegistry registry = plugin.getEntityElementProviderRegistry();
        registry.register(new DisplayElementProvider<>(itemDisplayType));
        registry.register(new DisplayElementProvider<>(blockDisplayType));
        registry.register(new DisplayElementProvider<>(textDisplayType));

        plugin.getCommandManager().parserRegistry().registerParserSupplier(TypeToken.get(BlockData.class),
                p -> new BlockDataArgumentParser<>());

        CloudBrigadierManager<EasCommandSender, ?> brigadierManager = plugin.getCommandManager().brigadierManager();
        if (brigadierManager != null) {
            try {
                BlockDataArgumentParser.registerBrigadier(brigadierManager);
            } catch (Throwable e) {
                plugin.getLogger().warning("Failed to register Brigadier mappings for block data arguments");
            }
        }

        plugin.getAnnotationParser().parse(new DisplayCommands(this));
    }

    public DisplayElementType<ItemDisplay> getItemDisplayType() {
        return itemDisplayType;
    }

    public DisplayElementType<BlockDisplay> getBlockDisplayType() {
        return blockDisplayType;
    }

    public DisplayElementType<TextDisplay> getTextDisplayType() {
        return textDisplayType;
    }
}
