package com.carthon.stoneage.items;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.setup.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Objects;

public class ItemFireStick extends Item {
    public ItemFireStick(Properties properties) {
        super(properties);
    }


    @Override
    public void onUseTick(World world, LivingEntity livingEntityIn, ItemStack stack, int countUse) {
        StoneAge.LOGGER.debug("Preparing for use");
//        if (hasTag(stack)) {
//            assert stack.getTag() != null;
//            if (stack.getTag().getInt("rubbingCount") != 0)
//                stack.getTag().putInt("rubbingCount", 0);
//            if (stack.getTag().getBoolean("animate"))
//                stack.getTag().putBoolean("animate", false);
//        }
        if (livingEntityIn instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntityIn;
            ItemStack mainHandItem = player.getItemInHand(Hand.MAIN_HAND);
            ItemStack offHandItem = player.getItemInHand(Hand.OFF_HAND);
            if (!mainHandItem.isEmpty() && !offHandItem.isEmpty()) {
                if (mainHandItem.getItem() == ModItems.FIRE_STICK.get() && offHandItem.getItem() == ModItems.FIRE_STICK.get()) {
                    int count = mainHandItem.getTag().getInt("rubbingCount");
                    StoneAge.LOGGER.debug("Doing Action and rubbingCount: " + count);
                    if (mainHandItem.getTag().getBoolean("animate")) {
                        mainHandItem.getTag().putInt("rubbingCount", count + 1);

                        if (count == 10 || count == 30 || count == 50) {
                            if (player.level.isClientSide)
                                player.swing(Hand.OFF_HAND);
                        }

                        if (count == 20 || count == 40 || count == 60) {
                            if (player.level.isClientSide)
                                player.swing(Hand.MAIN_HAND);
                        }
                    }
                    if (count >= getUseDuration(null) - 1) {
                        mainHandItem.getTag().putInt("rubbingCount", 0);
                        mainHandItem.getTag().putBoolean("animate", false);

                        BlockRayTraceResult mop = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
                        if (mop.getType() == RayTraceResult.Type.BLOCK) {
                            BlockPos clickPos = mop.getBlockPos();
                            if (world.mayInteract(player, clickPos)) {
                                //Antes ponia offset --> Revisar
                                BlockPos targetPos = clickPos.relative(mop.getDirection());
                                BlockState blockstate = AbstractFireBlock.getState(world, targetPos);

                                if (AbstractFireBlock.canBePlacedAt(world, targetPos, Direction.UP))
                                if (player.mayUseItemAt(targetPos, Direction.from3DDataValue(mop.subHit), mainHandItem)) {
                                    world.setBlock(targetPos, blockstate, 11);
                                    world.playSound(null, targetPos, SoundEvents.FIRECHARGE_USE, SoundCategory.BLOCKS, 0.2F, 0.1F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                                    if (player instanceof ServerPlayerEntity) {
                                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, targetPos, stack);
                                        player.getMainHandItem().hurtAndBreak(1, player, (item) -> {
                                            item.broadcastBreakEvent(player.getUsedItemHand());
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        LivingEntity player = context.getPlayer();
        ItemStack mainHandItem = Objects.requireNonNull(context.getPlayer()).getItemInHand(Hand.MAIN_HAND);
        ItemStack offHandItem = context.getPlayer().getItemInHand(Hand.OFF_HAND);
        if (!mainHandItem.isEmpty() && !offHandItem.isEmpty() && hasTag(mainHandItem)) {
            if (mainHandItem.getItem() == ModItems.FIRE_STICK.get() && offHandItem.getItem() ==  ModItems.FIRE_STICK.get()) {
                assert mainHandItem.getTag() != null;
                assert player != null;
                mainHandItem.getTag().putInt("rubbingCount", 0);
                mainHandItem.getTag().putBoolean("animate", true);
                player.startUsingItem(Hand.MAIN_HAND);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 60;
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
            assert stack.getTag() != null;
            stack.getTag().putInt("rubbingCount", 0);
            stack.getTag().putBoolean("animate", false);
            return false;
        }
        return true;
    }
}
