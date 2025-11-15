package de.arvitus.dragonegggame;

import de.arvitus.dragonegggame.api.APIUtils;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.config.Config;
import de.arvitus.dragonegggame.config.Data;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

import static de.arvitus.dragonegggame.DragonEggGame.CONFIG;

public class Placeholders {
    public static final Map<Identifier, PlaceholderHandler> PLACEHOLDERS = Map.of(
        modIdentifier("bearer"),
        (ctx, arg) -> PlaceholderResult.value(APIUtils.getBearer()),
        modIdentifier("exact_pos"),
        (ctx, arg) -> {
            if (!Permissions.check(ctx.source(), Perms.EXACT_POS_PLACEHOLDER, 4))
                return PlaceholderResult.invalid("No Permission");
            if (DragonEggAPI.getData() == null) return PlaceholderResult.invalid("No Data");
            return PlaceholderResult.value(DragonEggAPI.getData().getBlockPos().toShortString());
        },
        modIdentifier("randomized_pos"),
        (ctx, arg) -> {
            if (!Permissions.check(ctx.source(), Perms.RANDOMIZED_POS_PLACEHOLDER, 4))
                return PlaceholderResult.invalid("No Permission");
            if (DragonEggAPI.getData() == null) return PlaceholderResult.invalid("No Data");
            return PlaceholderResult.value(DragonEggAPI.getData().getRandomizedPosition().toShortString());
        },
        modIdentifier("pos"),
        (ctx, arg) -> {
            Data data = DragonEggAPI.getData();
            if (data == null) return PlaceholderResult.invalid("No Data");
            Config.VisibilityType visibilityType = CONFIG.getVisibility(data.type);
            return PlaceholderResult.value(
                switch (visibilityType) {
                    case EXACT -> data.getBlockPos().toShortString();
                    case RANDOMIZED -> data.getRandomizedPosition().toShortString();
                    case HIDDEN -> "Unknown";
                }
            );
        },
        modIdentifier("item"),
        (ctx, arg) -> {
            // from https://github.com/Patbox/TextPlaceholderAPI/blob/276a9c0f19e0ceed0140ce2e028fa438f3859632/src/main/java/eu/pb4/placeholders/impl/GeneralUtils.java#L188
            ItemStack stack = Items.DRAGON_EGG.getDefaultStack();
            MutableText mutableText = Text
                .empty()
                .append(stack.getName())
                .formatted(stack.getRarity().getFormatting())
                .styled(style -> style.withHoverEvent(new HoverEvent.ShowItem(stack)));
            return PlaceholderResult.value(mutableText);
        }
    );

    public static Identifier modIdentifier(String path) {
        return Identifier.of(DragonEggGame.MOD_ID_ALIAS, path);
    }

    public static void register() {
        PLACEHOLDERS.forEach(eu.pb4.placeholders.api.Placeholders::register);
    }
}
