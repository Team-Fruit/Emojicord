package net.teamfruit.emojicord.compat;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class CompatSign {

	public static class CompatMovingObjectPosition {
		// private final RayTraceResult movingPos;

		public CompatMovingObjectPosition(final RayTraceResult movingPos) {
			// this.movingPos = movingPos;
		}

		public static @Nullable CompatMovingObjectPosition getMovingPos() {
			final RayTraceResult movingPos = Compat.CompatMinecraft.getMinecraft().getMinecraftObj().objectMouseOver;
			return movingPos==null ? null : new CompatMovingObjectPosition(movingPos);
		}
	}

	public static class CompatBlockPos {
		public final @Nonnull BlockPos pos;

		public CompatBlockPos(final @Nonnull BlockPos pos) {
			Validate.notNull(pos, "MovePos needs position");
			this.pos = pos;
		}

		public int getX() {
			return this.pos.getX();
		}

		public int getY() {
			return this.pos.getY();
		}

		public int getZ() {
			return this.pos.getZ();
		}

		public static @Nonnull CompatBlockPos fromCoords(final int xCoord, final int yCoord, final int zCoord) {
			return new CompatBlockPos(new BlockPos(xCoord, yCoord, zCoord));
		}

		public static @Nonnull CompatBlockPos getTileEntityPos(@Nonnull final TileEntity tile) {
			return new CompatBlockPos(tile.getPos());
		}

		public void setTileEntityPos(@Nonnull final TileEntity tile) {
			tile.setPos(this.pos);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime*result+this.pos.hashCode();
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
			if (!this.pos.equals(other.pos))
				return false;
			return true;
		}
	}

	public static class CompatKeyRegistrar {
		public static void registerKeyBinding(final KeyBinding key) {
			ClientRegistry.registerKeyBinding(key);
		}
	}

	public static abstract class CompatTileEntitySignRenderer extends SignTileEntityRenderer {
		public void renderBaseTileEntityAt(final @Nullable SignTileEntity tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha) {
			super.render(tile, x, y, z, partialTicks, destroy);
		}

		public abstract void renderTileEntityAtCompat(final @Nullable SignTileEntity tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha);

		@Override
		public void render(final @Nullable SignTileEntity tile, final double x, final double y, final double z, final float partialTicks, final int destroy) {
			renderTileEntityAtCompat(tile, x, y, z, partialTicks, destroy, 1f);
		}
	}

	public static class CompatTileEntityRendererDispatcher {
		public static void renderTileEntityAt(final @Nullable SignTileEntity tile, final double x, final double y, final double z, final float partialTicks, final int destroy, final float alpha) {
			TileEntityRendererDispatcher.instance.render(tile, x, y, z, partialTicks, destroy, true);
		}
	}

	public static class CompatEntityPlayer {
		private final ClientPlayerEntity player;

		public CompatEntityPlayer(@Nonnull final ClientPlayerEntity player) {
			this.player = player;
		}

		public ClientPlayerEntity getPlayerObj() {
			return this.player;
		}

		public @Nullable ItemStack getHeldItemMainhand() {
			return this.player.getHeldItemMainhand();
		}

		public @Nullable ItemStack getHeldItemOffhand() {
			return this.player.getHeldItemOffhand();
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
			return this.world.getLightFor(LightType.SKY, pos.pos);
		}

		public CompatBlockState getBlockState(final CompatBlockPos pos) {
			return new CompatBlockState(this.world.getBlockState(pos.pos));
		}

		public @Nullable TileEntity getTileEntity(final CompatBlockPos pos) {
			return this.world.getTileEntity(pos.pos);
		}
	}

	public static class CompatBlockState {
		private final BlockState blockstate;

		public CompatBlockState(final BlockState blockstate) {
			this.blockstate = blockstate;
		}

		public BlockState getBlockStateObj() {
			return this.blockstate;
		}

		public CompatBlock getBlock() {
			return new CompatBlock(this.blockstate.getBlock());
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
			throw new NotImplementedException("canPlaceBlockAt");
		}
	}

	public static class CompatBlocks {
		public static final Block STANDING_SIGN = Blocks.OAK_SIGN;
		public static final Block WALL_SIGN = Blocks.OAK_WALL_SIGN;
	}

	public static class CompatItems {
		public static final Item SIGN = Items.OAK_SIGN;
	}

	public static class CompatSimpleNetworkWrapper {
		//private final SimpleNetworkWrapper network;

		//public CompatSimpleNetworkWrapper(final SimpleNetworkWrapper network) {
		//	this.network = network;
		//}

		public CompatSimpleNetworkWrapper(final Object network) {
			//this(network);
		}

		public void sendToServer(final CompatMessage message) {
			throw new NotImplementedException("sendToServer");
			//this.network.sendToServer(message.message);
		}
	}

	public static class CompatMessage {
		//public final IMessage message;

		//public CompatMessage(final IMessage message) {
		//	this.message = message;
		//}

		public CompatMessage(final Object message) {
			//this(message);
		}
	}

	public static class CompatNetHandlerPlayClient {
		private final ClientPlayNetHandler connection;

		public CompatNetHandlerPlayClient(final ClientPlayNetHandler connection) {
			this.connection = connection;
		}

		public void sendPacket(final CompatPacket packet) {
			this.connection.sendPacket(packet.packet);
		}
	}

	public static class CompatPacket {
		public final IPacket<? extends INetHandler> packet;

		public CompatPacket(final IPacket<? extends INetHandler> packet) {
			this.packet = packet;
		}
	}

	public static class CompatC12PacketUpdateSign extends CompatPacket {
		public CompatC12PacketUpdateSign(final CUpdateSignPacket packet) {
			super(packet);
		}

		public static CompatC12PacketUpdateSign create(final CompatBlockPos pos, final List<CompatChat.CompatTextComponent> clines) {
			final List<ITextComponent> lines = Lists.transform(clines, input -> {
				return input==null ? null : input.component;
			});
			return new CompatC12PacketUpdateSign(new CUpdateSignPacket(
					pos.pos,
					lines.stream().skip(0).findFirst().orElseGet(() -> new StringTextComponent("")),
					lines.stream().skip(1).findFirst().orElseGet(() -> new StringTextComponent("")),
					lines.stream().skip(2).findFirst().orElseGet(() -> new StringTextComponent("")),
					lines.stream().skip(3).findFirst().orElseGet(() -> new StringTextComponent(""))));
		}
	}

	public static class CompatC17PacketCustomPayload extends CompatPacket {
		public CompatC17PacketCustomPayload(final CCustomPayloadPacket packet) {
			super(packet);
		}

		public static CompatC17PacketCustomPayload create(final String channel, final String data) {
			return new CompatC17PacketCustomPayload(new CCustomPayloadPacket(new ResourceLocation(channel), new PacketBuffer(Unpooled.buffer()).writeString(data)));
		}
	}

	public static class CompatTileEntitySign {
		public static List<CompatChat.CompatTextComponent> getSignText(final SignTileEntity tile) {
			return Lists.transform(Lists.newArrayList(tile.signText), t -> new CompatChat.CompatTextComponent(t));
		}

		public static void setSignText(final SignTileEntity tile, final List<CompatChat.CompatTextComponent> clines) {
			final List<ITextComponent> lines = Lists.transform(clines, t -> t==null ? null : t.component);
			final Iterator<ITextComponent> itr = lines.iterator();
			for (int i = 0; i<tile.signText.length; i++)
				tile.signText[i] = itr.hasNext() ? itr.next() : CompatChat.CompatTextComponent.blank.component;
		}
	}

	public static class CompatChatRender {
		public static abstract class CompatPicChatLine extends ChatLine {
			public static final @Nonnull CompatChat.CompatTextComponent dummytext = CompatChat.CompatTextComponent.fromText("");

			public CompatPicChatLine(final int updateCounterCreated, final int lineId) {
				super(updateCounterCreated, dummytext.component, lineId);
			}

			public @Nullable ITextComponent onClicked(final @Nonnull NewChatGui chat, final int x) {
				final CompatChat.CompatTextComponent component = onClickedCompat(chat, x);
				if (component!=null)
					return component.component;
				return null;
			}

			public abstract @Nullable CompatChat.CompatTextComponent onClickedCompat(final @Nonnull NewChatGui chat, final int x);
		}
	}

	public static class CompatModel<T extends IModel<T>> {
		public final IModel<T> model;

		public CompatModel(@Nonnull final IModel<T> model) {
			this.model = model;
		}
	}

	public static class CompatBakedModel {
		public final IBakedModel bakedModel;

		public CompatBakedModel(@Nonnull final IBakedModel bakedModel) {
			this.bakedModel = bakedModel;
		}
	}

}
