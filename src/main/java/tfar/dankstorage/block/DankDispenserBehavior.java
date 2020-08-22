package tfar.dankstorage.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import tfar.dankstorage.tile.DockBlockEntity;

public class DankDispenserBehavior implements DispenserBehavior {

	@Override
	public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
		ServerWorld world = pointer.getWorld();
		BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
		BlockState state = world.getBlockState(blockPos);
		if (state.getBlock() instanceof DockBlock && state.get(DockBlock.TIER) == 0) {
			insertDank(world,blockPos,stack);
			return ItemStack.EMPTY;
		} else if (state.getBlock() instanceof DockBlock) {
			ItemStack old = removeDank(world,blockPos,stack);
			insertDank(world,blockPos,stack);
			return old;
		}
		return stack;
	}

	public ItemStack removeDank(ServerWorld world,BlockPos pos,ItemStack stack) {
		DockBlockEntity dockBlockEntity = (DockBlockEntity)world.getBlockEntity(pos);
		ItemStack old = dockBlockEntity.removeTank0();
		return old;
	}

	public void insertDank(ServerWorld world,BlockPos pos,ItemStack stack) {
		DockBlockEntity dockBlockEntity = (DockBlockEntity)world.getBlockEntity(pos);
		dockBlockEntity.addTank(stack);
	}
}
