package com.carthon.stoneage.setup;

import com.carthon.stoneage.StoneAge;
import com.carthon.stoneage.inventory.WorkStumpContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;

public class ModContainers {
    public static ContainerType<WorkStumpContainer> workStumpContainer;

    public static void register(RegistryEvent.Register<ContainerType<?>> registry){

    }

    public static <T extends Container>ContainerType<T> registerContainer(ContainerType<T> type, String id){
        type.setRegistryName(StoneAge.MOD_ID, id);
        return type;
    }
}
