package com.bmp.bigsaws.mixins;

import com.bmp.bigsaws.LargeStructureTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScatteredOreFeature.class)
public class ScatteredOreFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void preventScatteredOreInStructures(FeaturePlaceContext<OreConfiguration> context,
                                                  CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = context.origin();
        if (LargeStructureTracker.isPositionInsideAnyStructure(pos, 4)) {
            cir.setReturnValue(false);
        }
    }
}
