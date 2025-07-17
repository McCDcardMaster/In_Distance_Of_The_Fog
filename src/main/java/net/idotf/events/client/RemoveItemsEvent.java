package net.idotf.events.client;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RemoveItemsEvent {
    public static void removeRandomItem(EntityPlayerMP player) {
        IInventory inventory = player.inventory;
        List<Integer> nonEmptySlots = new ArrayList<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                nonEmptySlots.add(i);
            }
        }

        if (!nonEmptySlots.isEmpty()) {
            int randomSlot = nonEmptySlots.get(
                    ThreadLocalRandom.current().nextInt(nonEmptySlots.size())
            );
            inventory.setInventorySlotContents(randomSlot, ItemStack.EMPTY);
        }
    }
}