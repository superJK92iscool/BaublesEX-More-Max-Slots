package baubles.client.gui;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesItemHandler;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketIncrOffset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

public class GuiBaublesFlip extends GuiButton {

    protected final GuiContainer parentGui;
    private final boolean direction;
    private int ticks;
    private final BaublesItemHandler baubles;

    /**
     * Add PgDn and PgUp to Bauble slots.
     * @param direction False means PgDn, True means PgUp.
     */
    public GuiBaublesFlip(int id, GuiContainer parentGui, int x, int y, boolean direction) {
        super(id, x, y, 27, 14, "");
        this.parentGui = parentGui;
        this.direction = direction;
        this.baubles = (BaublesItemHandler) parentGui.mc.player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean pressed = super.mousePressed(mc, mouseX, mouseY);
        if (pressed) {
            int value = direction ? 1 : -1;
            ticks = 10;
            PacketHandler.INSTANCE.sendToServer(new PacketIncrOffset(value));
            baubles.incrOffset(value);
        }
        return pressed;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GuiPlayerExpanded.container1);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 200);

            if (ticks > 0) {
                this.drawTexturedModalRect(this.x, this.y, 176, 14 + (direction ? 28 : 0), 28, 14);
                ticks--;
            }
            else {
                this.drawTexturedModalRect(this.x, this.y, 176, (direction ? 28 : 0), 28, 14);
            }

            GlStateManager.popMatrix();

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
