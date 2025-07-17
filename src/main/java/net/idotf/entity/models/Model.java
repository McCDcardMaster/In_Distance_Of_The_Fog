package net.idotf.entity.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Model extends ModelBase {
    private final ModelRenderer RightArm;
    private final ModelRenderer RightLeg;
    private final ModelRenderer Head;
    private final ModelRenderer Body;
    private final ModelRenderer LeftArm;
    private final ModelRenderer LeftLeg;

    public Model() {
        textureWidth = 64;
        textureHeight = 32;

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        RightArm.cubeList.add(new ModelBox(RightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        RightLeg.cubeList.add(new ModelBox(RightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.cubeList.add(new ModelBox(Head, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.cubeList.add(new ModelBox(Body, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        LeftArm.cubeList.add(new ModelBox(LeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        LeftLeg.cubeList.add(new ModelBox(LeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, 
                     float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        
        Head.render(scale);
        Body.render(scale);
        RightArm.render(scale);
        LeftArm.render(scale);
        RightLeg.render(scale);
        LeftLeg.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, 
                                float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        Head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        Head.rotateAngleX = headPitch * ((float)Math.PI / 180F);

        float handSpeed = 0.1F;
        float xAmplitude = 0.035F;
        float zAmplitude = 0.035F;
        float phaseOffset = 0.7F;

        RightArm.rotateAngleX = MathHelper.cos(ageInTicks * handSpeed) * xAmplitude;
        RightArm.rotateAngleZ = MathHelper.sin(ageInTicks * handSpeed + phaseOffset) * zAmplitude;

        LeftArm.rotateAngleX = MathHelper.cos(ageInTicks * handSpeed + (float)Math.PI) * xAmplitude;
        LeftArm.rotateAngleZ = MathHelper.sin(ageInTicks * handSpeed + (float)Math.PI + phaseOffset) * zAmplitude;

        RightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        LeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}