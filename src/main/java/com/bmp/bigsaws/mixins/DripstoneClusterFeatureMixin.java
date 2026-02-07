package com.bmp.bigsaws.mixins;

import com.bmp.bigsaws.LargeStructureTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripstoneClusterFeature.class)
public class DripstoneClusterFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void preventDripstoneClusterInStructures(FeaturePlaceContext<DripstoneClusterConfiguration> context,
                                                      CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = context.origin();
        if (LargeStructureTracker.isPositionInsideAnyStructure(pos, 4)) {
            cir.setReturnValue(false);
        }
    }
}
