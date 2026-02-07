package com.bmp.jigsaw.expander;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(JigsawMod.MODID)
public class JigsawMod
{
    public static final String MODID = "jigsawexpander";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<LargeJigsawStructure>> LARGE_JIGSAW =
            STRUCTURE_TYPES.register("large_jigsaw", () -> () -> LargeJigsawStructure.CODEC);

    public JigsawMod(IEventBus modEventBus)
    {
        STRUCTURE_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Jigsaw Expander initialized");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LargeStructureTracker.clear();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event)
    {
        LargeStructureTracker.clear();
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
