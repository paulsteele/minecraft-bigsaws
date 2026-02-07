package com.bmp.jigsaw.expander.mixins;

import com.bmp.jigsaw.expander.LargeStructureTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.PointedDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PointedDripstoneFeature.class)
public class PointedDripstoneFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void preventPointedDripstoneInStructures(FeaturePlaceContext<PointedDripstoneConfiguration> context,
                                                      CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = context.origin();
        if (LargeStructureTracker.isPositionInsideAnyStructure(pos, 4)) {
            cir.setReturnValue(false);
        }
    }
}
