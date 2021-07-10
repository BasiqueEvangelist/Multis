package net.blancworks.multis.mixins;

import net.blancworks.multis.objects.item.MultisItem;
import net.blancworks.multis.objects.item.MultisItemManager;
import net.blancworks.multis.rendering.MultisRenderingManager;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V")
    private void renderItemHead(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        try {
            MultisItem item = MultisItemManager.getItemFromStack(stack);

            if (item != null && !stack.isEmpty() && item.model != null) {
                matrices.push();

                item.model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
                matrices.translate(-0.5D, -0.5D, -0.5D);

                RenderLayer itemLayer = RenderLayer.getItemEntityTranslucentCull(MultisRenderingManager.itemAtlasID);
                RenderLayer enchantLayer = RenderLayer.getEntityGlint();

                VertexConsumer consumer = stack.hasGlint() ? VertexConsumers.dual(vertexConsumers.getBuffer(itemLayer), vertexConsumers.getBuffer(enchantLayer)) : vertexConsumers.getBuffer(itemLayer);

                this.renderBakedItemModel(model, stack, light, overlay, matrices, consumer);

                matrices.pop();

                ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "renderBakedItemQuads", cancellable = true, at = @At("HEAD"))
    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay, CallbackInfo ci) {
        try {
            MultisItem item = MultisItemManager.getItemFromStack(stack);

            if (item != null && item.model != null && item.model.texture != null && item.model.texture.isReady) {

                item.model.texture.debug = MinecraftClient.getInstance().mouse.wasRightButtonClicked();
                item.model.render(matrices, vertices, overlay, light);

                ci.cancel();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "renderGuiItemModel", at = @At("HEAD"))
    private void renderGuiItemModelHead(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
        try {
            MultisItem item = MultisItemManager.getItemFromStack(stack);

            if (item != null && item.model != null && !item.model.isSideLit())
                DiffuseLighting.disableGuiDepthLighting();
        } catch (Throwable t) {

        }
    }

    @Inject(method = "renderGuiItemModel", at = @At("RETURN"))
    private void renderGuiItemModelTail(ItemStack stack, int x, int y, BakedModel model, CallbackInfo ci) {
        try {
            MultisItem item = MultisItemManager.getItemFromStack(stack);

            if (item != null && item.model != null && !item.model.isSideLit())
                DiffuseLighting.enableGuiDepthLighting();
        } catch (Throwable t) {

        }
    }
}
