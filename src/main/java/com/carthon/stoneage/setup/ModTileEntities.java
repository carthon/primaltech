package com.carthon.stoneage.setup;

import com.carthon.stoneage.tiles.ClayKilnTileEntity;
import com.carthon.stoneage.tiles.TreeStumpTileEntity;
import com.carthon.stoneage.tiles.WorkStumpTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModTileEntities {
    public static final RegistryObject<TileEntityType<WorkStumpTileEntity>> WORK_STUMP_TILE_ENTITY_TYPE =
        register("work_stump_block", () -> TileEntityType.Builder.of(WorkStumpTileEntity::new, ModBlocks.WORK_STUMP.get()).build(null));

    public static final RegistryObject<TileEntityType<TreeStumpTileEntity>> TREE_STUMP_TILE_ENTITY_TYPE =
        register("tree_stump_block", () -> TileEntityType.Builder.of(TreeStumpTileEntity::new, ModBlocks.TREE_STUMP.get()).build(null));

    public static final RegistryObject<TileEntityType<ClayKilnTileEntity>> CLAY_KILN_TILE_ENTITY_TYPE =
        register("clay_kiln_block", () -> TileEntityType.Builder.of(ClayKilnTileEntity::new, ModBlocks.CLAY_KILN_BLOCK.get()).build(null));

    public static void register() {}
    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<TileEntityType<T>> tileEntityType) {
        return Registration.TILE_ENTITY_TYPE.register(name, tileEntityType);
    }
}
