package baubles.client.gui;

import baubles.api.inv.InventoryEffectRendererEx;
import baubles.common.BaubleContent;
import baubles.common.container.ContainerBaublesTab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class GuiBaublesTab extends InventoryEffectRendererEx {
    public GuiBaublesTab(EntityPlayer player) {
        super(new ContainerBaublesTab(player.inventory, !player.getEntityWorld().isRemote, player));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(GuiPlayerExpanded.container1);
        // main gui
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        // offhand slot
        this.drawTexturedModalRect(this.guiLeft - 28, this.guiTop + 12, 176, 56, 28, 28);
        // bauble slot
        outerLoop:
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (j + i * 9 >= BaubleContent.getAmount()) break outerLoop;
                this.drawTexturedModalRect(this.guiLeft + 7 + j * 18, this.guiTop + 17 + i * 18, 181, 61, 18, 18);
            }
        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawActivePotionEffects() {
        super.guiTop += 44;
        super.drawActivePotionEffects();
        super.guiTop -= 44;
    }
}
