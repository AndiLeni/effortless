package dev.huskuraft.effortless.building.structure.builder.singles;

import java.util.stream.Stream;

import dev.huskuraft.effortless.api.core.BlockInteraction;
import dev.huskuraft.effortless.api.core.BlockPosition;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.building.Context;
import dev.huskuraft.effortless.building.structure.builder.SingleClickBuilder;

public class Single extends SingleClickBuilder {

    public static BlockInteraction traceSingle(Player player, Context context) {
        var interaction = player.raytrace(context.maxReachDistance(), 0, false);

        var startPos = interaction.getBlockPosition();

        var tracingAbsolute = context.isBreakingBlock() || context.replaceMode().isQuick();
        var replaceable = player.getWorld().getBlockState(startPos).isReplaceable(player, interaction);

        var tracingRelative = !tracingAbsolute && !replaceable;

        if (tracingRelative) {
            startPos = startPos.relative(interaction.getDirection());
        }
        return interaction.withPosition(startPos);
    }

    public static Stream<BlockPosition> collectSingleBlocks(Context context) {
        return Stream.of(context.firstBlockPosition());
    }

    @Override
    protected BlockInteraction traceFirstInteraction(Player player, Context context) {
        return traceSingle(player, context);
    }

    @Override
    protected Stream<BlockPosition> collectFinalBlocks(Context context) {
        return collectSingleBlocks(context);
    }

}
