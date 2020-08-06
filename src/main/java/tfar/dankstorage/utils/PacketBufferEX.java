package tfar.dankstorage.utils;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.network.PacketByteBuf;
import javax.annotation.Nullable;
import java.io.IOException;

public class PacketBufferEX {

  public static void writeExtendedItemStack(PacketByteBuf buf,ItemStack stack) {
    if (stack.isEmpty()) {
      buf.writeInt(-1);
    } else {
      buf.writeInt(Item.getRawId(stack.getItem()));
      buf.writeInt(stack.getCount());

      CompoundTag nbttagcompound = stack.getTag();

      writeNBT(buf,nbttagcompound);
    }
  }

  public static void writeNBT(PacketByteBuf buf, @Nullable CompoundTag nbt) {
    if (nbt == null) {
      buf.writeByte(0);
    } else {
      try {
        NbtIo.write(nbt, new ByteBufOutputStream(buf));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }

  public static ItemStack readExtendedItemStack(PacketByteBuf buf) {
    int i = buf.readInt();

    if (i < 0) {
      return ItemStack.EMPTY;
    } else {
      int j = buf.readInt();
      ItemStack itemstack = new ItemStack(Item.byRawId(i), j);
      itemstack.setTag(readNBT(buf));
      return itemstack;
    }
  }

  public static CompoundTag readNBT(PacketByteBuf buf) {
    int i = buf.readerIndex();
    byte b0 = buf.readByte();

    if (b0 == 0) {
      return null;
    } else {
      buf.readerIndex(i);
      try {
        return NbtIo.read(new ByteBufInputStream(buf), new PositionTracker(2097152L));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }

}

