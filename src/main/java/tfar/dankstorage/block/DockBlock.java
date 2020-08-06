package tfar.dankstorage.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.tile.DankBlockEntity;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DockBlock extends Block implements BlockEntityProvider {

  public static final IntProperty TIER = IntProperty.of("tier",0,7);

  public static final VoxelShape EMPTY;

  public static final VoxelShape DOCKED;

  static {
    VoxelShape a1 = Block.createCuboidShape(0,0,0,16,4,16);
    VoxelShape b1 = Block.createCuboidShape(4,0,4,12,4,12);
    VoxelShape shape1 = VoxelShapes.combine(a1,b1, BooleanBiFunction.NOT_SAME);

    VoxelShape a2 = Block.createCuboidShape(0,12,0,16,16,16);
    VoxelShape b2 = Block.createCuboidShape(4,12,4,12,16,12);
    VoxelShape shape2 = VoxelShapes.combine(a2,b2, BooleanBiFunction.NOT_SAME);

    VoxelShape p1 = Block.createCuboidShape(0,4,0,4,12,4);

    VoxelShape p2 = Block.createCuboidShape(12,4,0,16,12,4);

    VoxelShape p3 = Block.createCuboidShape(0,4,12,4,12,16);

    VoxelShape p4 = Block.createCuboidShape(12,4,12,12,12,16);

    EMPTY = VoxelShapes.union(shape1,shape2,p1,p2,p3,p4);

    DOCKED = VoxelShapes.union(EMPTY,Block.createCuboidShape(4,4,4,12,12,12));

  }

  public DockBlock(Settings p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    if (context instanceof EntityShapeContext && context instanceof PlayerEntity) {
      //PlayerEntity player = (PlayerEntity)context.getEntity();
      //if (player.getMainHandStack().getItem() instanceof DankItem)
     //   return DOCKED;
    }
    return state.get(TIER) > 0 ? DOCKED : EMPTY;
  }

  @Nonnull
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult p_225533_6_) {
    if (!world.isClient) {
      final BlockEntity tile = world.getBlockEntity(pos);
      if (tile instanceof DankBlockEntity) {
        ItemStack held = player.getStackInHand(hand);
        if (player.isInSneakingPose() && held.getItem().isIn(Utils.WRENCHES)) {
          world.breakBlock(pos, true, player);
          return ActionResult.SUCCESS;
        }

        if (held.getItem() instanceof DankItem) {

          if (state.get(TIER) > 0) {
            ((DankBlockEntity) tile).removeTank();
          }
          ((DankBlockEntity) tile).addTank(held);
          return ActionResult.SUCCESS;
        }

        if (held.isEmpty() && player.isSneaking()) {
          ((DankBlockEntity)tile).removeTank();
          return ActionResult.SUCCESS;
        }

        player.openHandledScreen((NamedScreenHandlerFactory) tile);
      }
    }
    return ActionResult.SUCCESS;
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    ItemStack bag = ctx.getStack();

    Block block = Block.getBlockFromItem(bag.getItem());
    if (block instanceof DockBlock)return block.getDefaultState();
    return block.getPlacementState(ctx);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new DankBlockEntity();
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(TIER);
  }


}