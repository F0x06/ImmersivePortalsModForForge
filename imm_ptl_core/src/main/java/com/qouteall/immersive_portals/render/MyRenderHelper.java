package com.qouteall.immersive_portals.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.qouteall.immersive_portals.CGlobal;
import com.qouteall.immersive_portals.CHelper;
import com.qouteall.immersive_portals.ClientWorldLoader;
import com.qouteall.immersive_portals.McHelper;
import com.qouteall.immersive_portals.OFInterface;
import com.qouteall.immersive_portals.portal.PortalLike;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE0;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glCullFace;

public class MyRenderHelper {
    
    public static final MinecraftClient client = MinecraftClient.getInstance();
    
    public static void drawFrameBufferUp(
        PortalLike portal,
        Framebuffer textureProvider,
        MatrixStack matrixStack
    ) {
        ShaderManager shaderManager = CGlobal.shaderManager;
        
        CHelper.checkGlError();
        McHelper.runWithTransformation(
            matrixStack,
            () -> {
                shaderManager.loadContentShaderAndShaderVars(0);
                
                if (OFInterface.isShaders.getAsBoolean()) {
                    GlStateManager.viewport(
                        0,
                        0,
                        PortalRenderer.client.getFramebuffer().viewportWidth,
                        PortalRenderer.client.getFramebuffer().viewportHeight
                    );
                }
                
                GlStateManager.enableTexture();
                GlStateManager.activeTexture(GL13.GL_TEXTURE0);
                
                textureProvider.beginRead();
                GlStateManager.texParameter(3553, 10241, 9729);
                GlStateManager.texParameter(3553, 10240, 9729);
                GlStateManager.texParameter(3553, 10242, 10496);
                GlStateManager.texParameter(3553, 10243, 10496);
                
                ViewAreaRenderer.drawPortalViewTriangle(portal, matrixStack, false, false);
                
                shaderManager.unloadShader();
                
                textureProvider.endRead();
                
                OFInterface.resetViewport.run();
            }
        );
        CHelper.checkGlError();
    }
    
    public static void renderScreenTriangle() {
        renderScreenTriangle(255, 255, 255, 255);
    }
    
    public static void renderScreenTriangle(Vec3d color) {
        renderScreenTriangle(
            (int) (color.x * 255),
            (int) (color.y * 255),
            (int) (color.z * 255),
            255
        );
    }
    
    public static void renderScreenTriangle(int r, int g, int b, int a) {
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        
        GlStateManager.disableAlphaTest();
        GlStateManager.disableTexture();
        
        GlStateManager.shadeModel(GL_SMOOTH);
        
        GL20.glUseProgram(0);
        GL11.glDisable(GL_CLIP_PLANE0);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_TRIANGLES, VertexFormats.POSITION_COLOR);
        
        bufferbuilder.vertex(1, -1, 0).color(r, g, b, a)
            .next();
        bufferbuilder.vertex(1, 1, 0).color(r, g, b, a)
            .next();
        bufferbuilder.vertex(-1, 1, 0).color(r, g, b, a)
            .next();
        
        bufferbuilder.vertex(-1, 1, 0).color(r, g, b, a)
            .next();
        bufferbuilder.vertex(-1, -1, 0).color(r, g, b, a)
            .next();
        bufferbuilder.vertex(1, -1, 0).color(r, g, b, a)
            .next();
        
        tessellator.draw();
        
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.popMatrix();
        
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.popMatrix();
        
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }
    
    /**
     * {@link Framebuffer#draw(int, int)}
     */
    public static void drawFrameBuffer(
        Framebuffer textureProvider,
        boolean doEnableAlphaTest,
        boolean doEnableModifyAlpha
    ) {
        CHelper.checkGlError();
        
        int viewportWidth = textureProvider.viewportWidth;
        int viewportHeight = textureProvider.viewportHeight;
        
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (doEnableModifyAlpha) {
            GlStateManager.colorMask(true, true, true, true);
        }
        else {
            GlStateManager.colorMask(true, true, true, false);
        }
        GlStateManager.disableDepthTest();
        GlStateManager.depthMask(false);
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double) viewportWidth, (double) viewportHeight, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
        GlStateManager.viewport(0, 0, viewportWidth, viewportHeight);
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        if (doEnableAlphaTest) {
            RenderSystem.enableAlphaTest();
        }
        else {
            GlStateManager.disableAlphaTest();
        }
        GlStateManager.disableBlend();
        GlStateManager.disableColorMaterial();
        
        
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureProvider.beginRead();
        float right = (float) viewportWidth;
        float up = (float) viewportHeight;
        float textureX = (float) textureProvider.viewportWidth / (float) textureProvider.textureWidth;
        float textureY = (float) textureProvider.viewportHeight / (float) textureProvider.textureHeight;
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        
        bufferBuilder.vertex(0.0D, (double) up, 0.0D)
            .texture(0.0F, 0.0F)
            .color(255, 255, 255, 255).next();
        
        bufferBuilder.vertex((double) right, (double) up, 0.0D)
            .texture(textureX, 0.0F)
            .color(255, 255, 255, 255).next();
        
        bufferBuilder.vertex((double) right, 0.0D, 0.0D)
            .texture(textureX, textureY)
            .color(255, 255, 255, 255).next();
        
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D)
            .texture(0.0F, textureY)
            .color(255, 255, 255, 255).next();
        
        tessellator.draw();
        textureProvider.endRead();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.popMatrix();
        
        CHelper.checkGlError();
    }
    
    public static void earlyUpdateLight() {
        if (!ClientWorldLoader.getIsInitialized()) {
            return;
        }
        
        ClientWorldLoader.getClientWorlds().forEach(world -> {
            if (world != MinecraftClient.getInstance().world) {
                int updateNum = world.getChunkManager().getLightingProvider().doLightUpdates(
                    1000, true, true
                );
            }
        });
    }
    
    public static void applyMirrorFaceCulling() {
        glCullFace(GL_FRONT);
    }
    
    public static void recoverFaceCulling() {
        glCullFace(GL_BACK);
    }
    
    public static void clearAlphaTo1(Framebuffer mcFrameBuffer) {
        mcFrameBuffer.beginWrite(true);
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0, 0, 0, 1.0f);
        RenderSystem.clear(GL_COLOR_BUFFER_BIT, true);
        RenderSystem.colorMask(true, true, true, true);
    }
    
    public static void restoreViewPort() {
        MinecraftClient client = MinecraftClient.getInstance();
        GlStateManager.viewport(
            0,
            0,
            client.getWindow().getFramebufferWidth(),
            client.getWindow().getFramebufferHeight()
        );
    }
}
