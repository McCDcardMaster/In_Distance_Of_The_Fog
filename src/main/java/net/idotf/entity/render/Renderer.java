package net.idotf.entity.render;

import net.idotf.entity.herobrine;
import net.idotf.entity.models.Model;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class Renderer extends RenderLiving<herobrine> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("idotf", "mob/herobrine.png");

    public Renderer(RenderManager manager) {
        super(manager, new Model(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(herobrine entity) {
        return TEXTURE;
    }
}