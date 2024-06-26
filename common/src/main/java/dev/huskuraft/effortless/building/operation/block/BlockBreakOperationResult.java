package dev.huskuraft.effortless.building.operation.block;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.building.operation.ItemStackUtils;
import dev.huskuraft.effortless.building.operation.ItemType;
import dev.huskuraft.effortless.building.operation.TransformableOperation;
import dev.huskuraft.effortless.building.operation.empty.EmptyOperation;

public class BlockBreakOperationResult extends BlockOperationResult {

    public BlockBreakOperationResult(
            BlockBreakOperation operation,
            Type result,
            List<ItemStack> inputs,
            List<ItemStack> outputs
    ) {
        super(operation, result, inputs, outputs);
    }

    @Override
    public TransformableOperation getReverseOperation() {
        if (result().fail()) {
            return new EmptyOperation();
        }

        return new BlockPlaceOperation(
                operation.getWorld(),
                operation.getPlayer(),
                operation.getContext(),
                operation.getStorage(),
                operation.getInteraction(),
                operation.getBlockState()
        );
    }

    @Override
    public List<ItemStack> getProducts(ItemType type) {
        return switch (type) {
            case PLAYER_USED -> {
                yield Collections.emptyList();
            }
            case WORLD_DROPPED -> {
                if (result.success()) {
                    var color = Color.RED;
                    if (color != null) {
                        yield outputs().stream().map(stack -> ItemStackUtils.putColorTag(stack, color.getRGB())).toList();
                    }
                }
                yield Collections.emptyList();
            }
        };
    }

    public Color getColor() {
        return switch (result) {
            case SUCCESS, CONSUME -> BLOCK_BREAK_OP_COLOR;
            default -> null;
        };
    }

}
