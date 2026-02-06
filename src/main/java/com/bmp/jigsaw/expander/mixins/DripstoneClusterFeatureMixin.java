package com.bmp.jigsaw.expander.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(DripstoneClusterFeature.class)
public class DripstoneClusterFeatureMixin {

    @Unique
    private static boolean isCobbledCitiesStructure(WorldGenLevel level, Structure structure) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation key = registry.getKey(structure);
        return key != null && key.getNamespace().equals("cobbled-cities");
    }

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void preventDripstoneClusterInStructures(FeaturePlaceContext<DripstoneClusterConfiguration> context,
                                                      CallbackInfoReturnable<Boolean> cir) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        StructureManager structureManager = level.getLevel().structureManager();
        ChunkPos chunkPos = new ChunkPos(pos);

        for (StructureStart start : structureManager.startsForStructure(
                chunkPos,
                structure -> isCobbledCitiesStructure(level, structure))) {

            BoundingBox box = start.getBoundingBox();

            if (box.inflatedBy(4).isInside(pos)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
