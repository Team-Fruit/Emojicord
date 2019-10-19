package net.teamfruit.emojicord.compat;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.Charsets;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.teamfruit.emojicord.CoreInvoke;

public class CompatSign {

	public static class CompatBlockPos {
		private final int x;
		private final int y;
		private final int z;

		public CompatBlockPos(final int x, final int y, final int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
		}

		public @Nonnull CompatBlockPos offset(final CompatEnumFacing facing) {
			if (facing==null)
				return this;
			return fromCoords(getX()+facing.offsetX, getY()+facing.offsetY, getZ()+facing.offsetZ);
		}

		public static @Nonnull CompatBlockPos fromCoords(final int xCoord, final int yCoord, final int zCoord) {
			return new CompatBlockPos(xCoord, yCoord, zCoord);
		}

		public static @Nonnull CompatBlockPos getTileEntityPos(@Nonnull final TileEntity tile) {
			return new CompatBlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
		}

		public void setTileEntityPos(@Nonnull final TileEntity tile) {
			tile.xCoord = getX();
			tile.yCoord = getY();
			tile.zCoord = getZ();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+this.x;
			result = prime*result+this.y;
			result = prime*result+this.z;
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this==obj)
				return true;
			if (obj==null)
				return false;
			if (!(obj instanceof CompatBlockPos))
				return false;
			final CompatBlockPos other = (CompatBlockPos) obj;
			if (this.x!=other.x)
				return false;
			if (this.y!=other.y)
				return false;
			if (this.z!=other.z)
				return false;
			return true;
		}
	}

	public static class CompatKeyRegistrar {
		public static void registerKeyBinding(final KeyBinding key) {
			ClientRegistry.registerKeyBinding(key);
		}
	}

	public static abstract class CompatTileEntitySignRenderer extends TileEntitySignRenderer {
		public void renderBaseTileEntityAt(final @Nullable TileEntitySign tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha) {
			super.renderTileEntityAt(tile, x, y, z, partialTicks);
		}

		public abstract void renderTileEntityAtCompat(final @Nullable TileEntitySign tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha);

		@Override
		public void renderTileEntityAt(final @Nullable TileEntitySign tile, final double x, final double y, final double z, final float partialTicks) {
			renderTileEntityAtCompat(tile, x, y, z, partialTicks, -1, 1f);
		}

		@Override
		public void renderTileEntityAt(final @Nullable TileEntity tile, final double x, final double y, final double z, final float partialTicks) {
			if (tile instanceof TileEntitySign)
				renderTileEntityAt((TileEntitySign) tile, x, y, z, partialTicks);
		}
	}

	public static class CompatTileEntityRendererDispatcher {
		public static void renderTileEntityAt(final @Nullable TileEntitySign tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha) {
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, x, y, z, partialTicks);
		}
	}

	public static class CompatEntityPlayer {
		private final EntityPlayer player;

		public CompatEntityPlayer(@Nonnull final EntityPlayer player) {
			this.player = player;
		}

		public EntityPlayer getPlayerObj() {
			return this.player;
		}

		public @Nullable ItemStack getHeldItemMainhand() {
			return this.player.getCurrentEquippedItem();
		}

		public @Nullable ItemStack getHeldItemOffhand() {
			return null;
		}
	}

	public static class CompatWorld {
		private final World world;

		public CompatWorld(@Nonnull final World world) {
			this.world = world;
		}

		public World getWorldObj() {
			return this.world;
		}

		public int getLightFor(final CompatBlockPos pos) {
			return this.world.getLightBrightnessForSkyBlocks(pos.getX(), pos.getY(), pos.getZ(), 0);
		}

		public CompatBlockState getBlockState(final CompatBlockPos pos) {
			return new CompatBlockState(this.world.getBlock(pos.getX(), pos.getY(), pos.getZ()));
		}

		public @Nullable TileEntity getTileEntity(final CompatBlockPos pos) {
			return this.world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static class CompatBlockState {
		private final Block blockstate;

		public CompatBlockState(final Block blockstate) {
			this.blockstate = blockstate;
		}

		public CompatBlock getBlock() {
			return new CompatBlock(this.blockstate);
		}

		public Material getMaterial() {
			return this.blockstate.getMaterial();
		}
	}

	public static class CompatBlock {
		private final Block block;

		public CompatBlock(final Block block) {
			this.block = block;
		}

		public Block getBlockObj() {
			return this.block;
		}

		public boolean canPlaceBlockAt(final CompatWorld world, final CompatBlockPos pos) {
			return this.block.canPlaceBlockAt(world.getWorldObj(), pos.getX(), pos.getY(), pos.getX());
		}
	}

	public static class CompatBlocks {
		public static final Block STANDING_SIGN = Blocks.standing_sign;
		public static final Block WALL_SIGN = Blocks.wall_sign;
	}

	public static class CompatItems {
		public static final Item SIGN = Items.sign;
	}

	public static class CompatSimpleNetworkWrapper {
		private final SimpleNetworkWrapper network;

		public CompatSimpleNetworkWrapper(final SimpleNetworkWrapper network) {
			this.network = network;
		}

		public CompatSimpleNetworkWrapper(final Object network) {
			this((SimpleNetworkWrapper) network);
		}

		public void sendToServer(final CompatMessage message) {
			this.network.sendToServer(message.message);
		}
	}

	public static class CompatMessage {
		public final IMessage message;

		public CompatMessage(final IMessage message) {
			this.message = message;
		}

		public CompatMessage(final Object message) {
			this((IMessage) message);
		}
	}

	public static class CompatNetHandlerPlayClient {
		private final NetHandlerPlayClient connection;

		public CompatNetHandlerPlayClient(final NetHandlerPlayClient connection) {
			this.connection = connection;
		}

		public void sendPacket(final CompatPacket packet) {
			this.connection.addToSendQueue(packet.packet);
		}
	}

	public static class CompatPacket {
		public final Packet packet;

		public CompatPacket(final Packet packet) {
			this.packet = packet;
		}
	}

	public static class CompatC12PacketUpdateSign extends CompatPacket {
		public CompatC12PacketUpdateSign(final C12PacketUpdateSign packet) {
			super(packet);
		}

		public static CompatC12PacketUpdateSign create(final CompatBlockPos pos, final List<CompatChat.CompatTextComponent> clines) {
			final List<String> lines = Lists.transform(clines, input -> {
				return input==null ? null : input.component.getUnformattedText();
			});
			return new CompatC12PacketUpdateSign(new C12PacketUpdateSign(pos.getX(), pos.getY(), pos.getZ(), lines.toArray(new String[lines.size()])));
		}
	}

	public static class CompatC17PacketCustomPayload extends CompatPacket {
		public CompatC17PacketCustomPayload(final C17PacketCustomPayload packet) {
			super(packet);
		}

		public static CompatC17PacketCustomPayload create(final String channel, final String data) {
			return new CompatC17PacketCustomPayload(new C17PacketCustomPayload(channel, data.getBytes(Charsets.UTF_8)));
		}
	}

	public static class CompatTileEntitySign {
		public static List<CompatChat.CompatTextComponent> getSignText(final TileEntitySign tile) {
			return Lists.transform(Lists.newArrayList(tile.signText), t -> CompatChat.CompatTextComponent.fromText(t));
		}

		public static void setSignText(final TileEntitySign tile, final List<CompatChat.CompatTextComponent> clines) {
			final List<String> lines = Lists.transform(clines, t -> t==null ? null : t.getUnformattedText());
			final Iterator<String> itr = lines.iterator();
			for (int i = 0; i<tile.signText.length; i++)
				tile.signText[i] = itr.hasNext() ? itr.next() : "";
		}
	}

	public static enum CompatEnumFacing {
		DOWN(0, -1, 0, EnumFacing.DOWN),
		UP(0, 1, 0, EnumFacing.UP),
		NORTH(0, 0, -1, EnumFacing.NORTH),
		SOUTH(0, 0, 1, EnumFacing.SOUTH),
		WEST(-1, 0, 0, EnumFacing.WEST),
		EAST(1, 0, 0, EnumFacing.EAST);

		public final int offsetX;
		public final int offsetY;
		public final int offsetZ;
		private final EnumFacing facing;

		private CompatEnumFacing(final int offsetX, final int offsetY, final int offsetZ, final EnumFacing facing) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.facing = facing;
		}

		public int getIndex() {
			return ordinal();
		}

		public static @Nonnull CompatEnumFacing fromFacing(@Nonnull final EnumFacing facing) {
			for (final CompatEnumFacing cfacing : values())
				if (facing==cfacing.facing)
					return cfacing;
			return DOWN;
		}

		public static @Nonnull CompatEnumFacing fromFacingId(final int facing) {
			final CompatEnumFacing[] cfacings = values();
			if (0<=facing&&facing<cfacings.length)
				return cfacings[facing];
			return DOWN;
		}
	}

	public static class CompatChatRender {
		public static abstract class CompatPicChatLine extends ChatLine {
			public static final @Nonnull CompatChat.CompatTextComponent dummytext = CompatChat.CompatTextComponent.fromText("");

			public CompatPicChatLine(final int updateCounterCreated, final int lineId) {
				super(updateCounterCreated, dummytext.component, lineId);
			}

			@CoreInvoke
			public @Nullable IChatComponent onClicked(final @Nonnull GuiNewChat chat, final int x) {
				final CompatChat.CompatTextComponent component = onClickedCompat(chat, x);
				if (component!=null)
					return component.component;
				return null;
			}

			public abstract @Nullable CompatChat.CompatTextComponent onClickedCompat(final @Nonnull GuiNewChat chat, final int x);
		}
	}

	public static class CompatModel {
	}

	public static class CompatBakedModel {
	}

}
