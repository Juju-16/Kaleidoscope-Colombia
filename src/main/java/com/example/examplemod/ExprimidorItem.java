package com.example.examplemod;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseAnim;

public class ExprimidorItem extends Item {

    public ExprimidorItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(net.minecraft.world.item.ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(net.minecraft.world.item.ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack limonSecundario = player.getOffhandItem();

        if (!limonSecundario.is(ExampleMod.LIMON_CORTADO.get())) {
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(
            ItemStack stack,
            Level level,
            net.minecraft.world.entity.LivingEntity entity,
            int timeLeft
    ) {
        int tiempoUsado = getUseDuration(stack) - timeLeft;

        if (tiempoUsado < 20) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        if (level.isClientSide) {
            return;
        }

        ItemStack limon = player.getOffhandItem();

        if (!limon.is(ExampleMod.LIMON_CORTADO.get())) {
            return;
        }

        level.playSound(
                null,
                entity.blockPosition(),
                ExampleMod.EXPRIMIR_LIMON.get(),
                net.minecraft.sounds.SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        // Consumir un limón cortado
        if (!player.getAbilities().instabuild) {
            limon.shrink(1);
        }

        // Crear el jugo de limón
        ItemStack jugo = new ItemStack(ExampleMod.JUGO_DE_LIMON.get());

        // Entregar el jugo al jugador
        if (!player.getInventory().add(jugo)) {
            player.drop(jugo, false);
        }
    }
}

