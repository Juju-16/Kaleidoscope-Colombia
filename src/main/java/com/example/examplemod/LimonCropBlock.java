package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LimonCropBlock extends CropBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF =
            BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty FRUIT =
            BooleanProperty.create("fruit");

    public LimonCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(FRUIT, false));
    }

    @Override
    protected Item getBaseSeedId() {
        return ExampleMod.SEMILLAS_DE_LIMON.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_7;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }
    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF, FRUIT);
    }
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        // La parte superior no crece por separado.
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return;
        }

        // Si está maduro y no tiene frutos, esperamos su regeneración.
        if (state.getValue(AGE) == 7 && !state.getValue(FRUIT)) {

            if (random.nextInt(5) == 0) {
                BlockState newState = state.setValue(FRUIT, true);

                level.setBlock(pos, newState, 2);

                BlockPos upperPos = pos.above();
                BlockState upperState = level.getBlockState(upperPos);

                if (upperState.is(this)
                        && upperState.getValue(HALF) == DoubleBlockHalf.UPPER) {

                    level.setBlock(
                            upperPos,
                            upperState.setValue(FRUIT, true),
                            2
                    );
                }
            }

            return;
        }

        // Antes de pasar de la etapa 4 a la 5, comprobamos el espacio.
        if (state.getValue(AGE) == 4 && !level.isEmptyBlock(pos.above())) {
            return;
        }

        // Crecimiento normal.
        super.randomTick(state, level, pos, random);

        BlockState newState = level.getBlockState(pos);

        // A partir de la etapa 5 necesitamos una parte superior.
        if (newState.is(this) && newState.getValue(AGE) >= 5) {

            BlockPos upperPos = pos.above();
            BlockState upperState = level.getBlockState(upperPos);

            BlockState desiredUpper = newState
                    .setValue(HALF, DoubleBlockHalf.UPPER);
            if (newState.getValue(AGE) == 7) {
                newState = newState.setValue(FRUIT, true);
                level.setBlock(pos, newState, 2);

                desiredUpper = newState.setValue(HALF, DoubleBlockHalf.UPPER);
            }

            if (upperState.is(this)
                    && upperState.getValue(HALF) == DoubleBlockHalf.UPPER) {

                level.setBlock(upperPos, desiredUpper, 2);

            } else if (level.isEmptyBlock(upperPos)) {

                level.setBlock(upperPos, desiredUpper, 3);
            }

            // Los primeros frutos aparecen al alcanzar la etapa 7.
            if (newState.getValue(AGE) == 7
                    && !newState.getValue(FRUIT)) {

                newState = newState.setValue(FRUIT, true);
                level.setBlock(pos, newState, 2);

                BlockState upper = level.getBlockState(upperPos);

                if (upper.is(this)) {
                    level.setBlock(
                            upperPos,
                            upper.setValue(FRUIT, true),
                            2
                    );
                }
            }
        }
    }


    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {

        // Si se aplica polvo de hueso a la parte superior,
        // trasladamos la posición a la parte inferior.
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        if (!state.is(this)) {
            return;
        }

        int currentAge = state.getValue(AGE);

        if (currentAge >= getMaxAge()) {
            return;
        }

        int newAge = currentAge + 1;

        BlockPos upperPos = pos.above();

        if (newAge >= 5) {
            BlockState upperState = level.getBlockState(upperPos);

            boolean validSpace = level.isEmptyBlock(upperPos)
                    || (upperState.is(this)
                    && upperState.getValue(HALF) == DoubleBlockHalf.UPPER);

            if (!validSpace) {
                return;
            }
        }

        BlockState newLowerState = state
                .setValue(AGE, newAge)
                .setValue(HALF, DoubleBlockHalf.LOWER);
        if (newAge == 7) {
            newLowerState = newLowerState.setValue(FRUIT, true);
        }

        level.setBlock(pos, newLowerState, 2);

        if (newAge >= 5) {
            BlockState newUpperState = newLowerState
                    .setValue(HALF, DoubleBlockHalf.UPPER);

            level.setBlock(upperPos, newUpperState, 3);
        }
    }
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        if (!state.is(this)) {
            return false;
        }

        int age = state.getValue(AGE);

        if (age >= getMaxAge()) {
            return false;
        }

        if (age < 5) {
            return true;
        }

        BlockState upperState = level.getBlockState(pos.above());

        return upperState.is(this)
                && upperState.getValue(HALF) == DoubleBlockHalf.UPPER
                && upperState.getValue(AGE) == age;
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState belowState = level.getBlockState(pos.below());

            return belowState.is(this)
                    && belowState.getValue(HALF) == DoubleBlockHalf.LOWER
                    && belowState.getValue(AGE) == state.getValue(AGE);
        }

        return super.canSurvive(state, level, pos);
    }
    @Override
    public void playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            net.minecraft.world.entity.player.Player player
    ) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);

            if (belowState.is(this)
                    && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.destroyBlock(belowPos, false);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }
    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        if (!state.is(this)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(AGE) < 7 || !state.getValue(FRUIT)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {

            level.playSound(
                    null,
                    pos,
                    net.minecraft.sounds.SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );

            Block.popResource(
                    level,
                    pos,
                    new ItemStack(ExampleMod.LIMON.get(), 8)
            );

            // Quitamos los frutos sin modificar la etapa.
            level.setBlock(
                    pos,
                    state.setValue(FRUIT, false),
                    2
            );

            BlockPos upperPos = pos.above();
            BlockState upperState = level.getBlockState(upperPos);

            if (upperState.is(this)
                    && upperState.getValue(HALF) == DoubleBlockHalf.UPPER) {
                level.setBlock(
                        upperPos,
                        upperState.setValue(FRUIT, false),
                        2
                );
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}