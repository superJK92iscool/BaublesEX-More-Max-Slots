package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.BaublesContainer;
import baubles.common.BaubleContent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBaublesTab extends BaublesContainer {
    private final IBaublesItemHandler baubles;
    private final EntityPlayer player;

    public ContainerBaublesTab(InventoryPlayer playerInv, boolean world, EntityPlayer player) {
        baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
        this.player = player;

        //add bauble slots (amount)
        outerLoop:
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (j + i * 9 >= BaubleContent.getAmount()) break outerLoop;
                this.addSlotToContainer(new SlotBaubleHandler(player, baubles, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        //add inventory upper half (27)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        //add inventory downer half (9)
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        //add offhand slot (1)
        this.addSlotToContainer(new Slot(playerInv, 40, -22, 18) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack);
            }

            @Override
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });
    }


    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack oldStack = slot.getStack();
            newStack = oldStack.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(newStack);
            int slotShift = baubles.getSlots();
            boolean isMerge = false;

            // baubles -> inv
            if (index < slotShift) {
                 isMerge = this.mergeItemStack(oldStack, slotShift, 36 + slotShift, false);
            }
            // inv -> offhand
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45 + slotShift).getHasStack()) {
                isMerge = this.mergeItemStack(oldStack, 36 + slotShift, 37 + slotShift, false);
            }
            // inv -> bauble
            else if (newStack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
                IBauble bauble = newStack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                isMerge = bauble.canEquip(oldStack, player) && this.mergeItemStack(oldStack, 0, slotShift,false);
            }

            if (!isMerge) {
                // upper -> downer
                if (index < 27 + slotShift) {
                    isMerge = this.mergeItemStack(oldStack, 27 + slotShift, 36 + slotShift, false);
                }
                // downer -> upper
                else if (index < 36 + slotShift) {
                    isMerge = this.mergeItemStack(oldStack, slotShift, 27 + slotShift, false);
                }
                // else
                else {
                    isMerge = this.mergeItemStack(oldStack, slotShift, 36 + slotShift, false);
                }
            }

            if (!isMerge) return ItemStack.EMPTY;


            if (oldStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.putStack(oldStack);
                slot.onSlotChanged();
            }


            if (oldStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }


            if (oldStack.isEmpty() && !baubles.isEventBlocked() && slot instanceof SlotBaubleHandler) {
                IBauble cap = newStack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                if (cap != null)
                    cap.onUnequipped(newStack, playerIn);
            }
        }

        return newStack;
    }
}
