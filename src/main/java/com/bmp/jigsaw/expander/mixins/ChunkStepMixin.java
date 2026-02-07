package com.bmp.jigsaw.expander.mixins;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep.Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Builder.class)
public class ChunkStepMixin {

    @ModifyVariable(method = "addRequirement", at = @At("HEAD"), argsOnly = true, index = 2)
    private int addRequirement(int radius, ChunkStatus status)
    {
        if (status == ChunkStatus.STRUCTURE_STARTS){
            return 32;
        }

        return radius;
    }
}
