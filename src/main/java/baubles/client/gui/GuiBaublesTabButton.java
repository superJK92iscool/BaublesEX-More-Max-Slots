package baubles.client.gui;

import baubles.api.inv.GuiButtonEx;
import baubles.common.Config;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesTab;
import baubles.common.network.PacketOpenNormalInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiBaublesTabButton extends GuiButtonEx {

    private final GuiContainer parentGui;
    private final int tabX;
    private final int tabId;

    public GuiBaublesTabButton(int buttonId, GuiContainer parentGui, int x, int tabId) {
        super(buttonId, parentGui.getGuiLeft() + x, parentGui.getGuiTop() - 28, 28, 26, "");
        this.parentGui = parentGui;
        this.tabX = x;
        this.tabId = tabId;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (this.hovered && enabled) {
            if (!(parentGui instanceof GuiBaublesTab) && tabId == 1) {
                PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesTab());
            }
            if (!(parentGui instanceof GuiInventory) && tabId == 0) {
                mc.displayGuiScreen(new GuiInventory(mc.player));
                PacketHandler.INSTANCE.sendToServer(new PacketOpenNormalInventory());
            }
        }
        return this.hovered;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float f) {
        if (this.visible) {
            int x = tabX + this.parentGui.getGuiLeft();

            int onTab = -1;
            if (parentGui instanceof GuiInventory) onTab = 0;
            if (parentGui instanceof GuiBaublesTab) onTab = 1;

            boolean onSide = (Config.invPosX == 0 && tabId == 0) || (Config.babPosX == 0 && tabId == 1);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(GuiPlayerExpanded.container1);
            this.drawTexturedModalRect(x, y, 0, 166 + (onTab == tabId ? 0 : 28), 28, 28);
            this.drawTexturedModalRect(x, y + 28, 56 + (onSide ? 0 : 28), 166 + (onTab == tabId ? 0 : 4), 28, 4);

            if (tabId == 0) {
                ItemStack chest = new ItemStack(Item.getItemFromBlock(Blocks.CHEST));
                drawIcon(mc, chest, x, y, (onTab == tabId));
            }
            if (tabId == 1) {
                this.drawTexturedModalRect(x + 6, y + 10 + (onTab == tabId ? 0 : 2), 176, 94, 15, 12);
            }

            this.hovered = x <= mouseX && mouseX < x + this.width && this.y <= mouseY && mouseY< this.y + this.height;

            if (this.hovered) {
                if (tabId == 0) {
                    drawHoveringText(mc, I18n.format("itemGroup.inventory"), mouseX, mouseY);
                }
                if (tabId == 1) {
                    drawHoveringText(mc, I18n.format("button.baubles"), mouseX, mouseY);
                }
            }
        }
    }
}
