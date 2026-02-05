package com.bmp.jigsaw.expander.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(LakeFeature.class)
public class LakeFeatureMixin {

    @Unique
    private static boolean isCobbledCitiesStructure(WorldGenLevel level, Structure structure) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation key = registry.getKey(structure);
        return key != null && key.getNamespace().equals("cobbled-cities");
    }

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void preventLakeInStructures(FeaturePlaceContext<LakeFeature.Configuration> context,
                                          CallbackInfoReturnable<Boolean> cir) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        StructureManager structureManager = level.getLevel().structureManager();
        ChunkPos chunkPos = new ChunkPos(pos);

        // Check if this position is inside cobbled-cities structures only
        for (StructureStart start : structureManager.startsForStructure(
                chunkPos,
                structure -> isCobbledCitiesStructure(level, structure))) {

            BoundingBox box = start.getBoundingBox();

            // Expand the bounding box slightly to catch lakes at edges
            if (box.inflatedBy(8).isInside(pos)) {
                // Cancel lake placement
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
