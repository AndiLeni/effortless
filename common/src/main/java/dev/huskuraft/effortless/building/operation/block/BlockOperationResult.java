package dev.huskuraft.effortless.building.operation.block;

import java.awt.*;
import java.util.List;

import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.building.operation.OperationResult;

public abstract class BlockOperationResult extends OperationResult {

    public static final Color BLOCK_BREAK_OP_COLOR = new Color(1f, 0, 0, 0.5f);
    public static final Color BLOCK_PLACE_SUCC_OP_COLOR = new Color(235, 235, 235);
    public static final Color BLOCK_PLACE_FAIL_OP_COLOR = new Color(255, 0, 0);

    protected final BlockOperation operation;
    protected final Type result;
    protected final List<ItemStack> inputs;
    protected final List<ItemStack> outputs;

    protected BlockOperationResult(
            BlockOperation operation,
            Type result,
            List<ItemStack> inputs,
            List<ItemStack> outputs
    ) {
        this.operation = operation;
        this.result = result;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public BlockOperation getOperation() {
        return operation;
    }

    public Type result() {
        return result;
    }

    public List<ItemStack> inputs() {
        return inputs;
    }

    public List<ItemStack> outputs() {
        return outputs;
    }

    public abstract Color getColor();

    public enum Type {
        SUCCESS,
        CONSUME,

        FAIL_PLAYER_CANNOT_MODIFY,
        FAIL_PLAYER_CANNOT_ATTACK,
        FAIL_PLAYER_CANNOT_BREAK,
        FAIL_PLAYER_CANNOT_INTERACT,

        FAIL_ITEM_INSUFFICIENT,
        FAIL_ITEM_NOT_BLOCK,

        FAIL_BLOCK_STATE_AIR,
        FAIL_BLOCK_STATE_NULL,

        FAIL_INTERNAL,

        FAIL_UNKNOWN;

        public boolean consumesAction() {
            return this == SUCCESS || this == CONSUME;
        }

        public boolean success() {
            return this == SUCCESS || this == CONSUME;
        }

        public boolean fail() {
            return this != SUCCESS && this != CONSUME;
        }

        public boolean shouldSwing() {
            return this == SUCCESS;
        }

        public boolean shouldAwardStats() {
            return this == SUCCESS;
        }
    }
}
