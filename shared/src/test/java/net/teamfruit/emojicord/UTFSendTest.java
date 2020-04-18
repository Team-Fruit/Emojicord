package net.teamfruit.emojicord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Timer;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.teamfruit.emojicord.compat.Compat.CompatMinecraft;

public class UTFSendTest {
	int state = -1;
	Timer timer = new Timer();
	String lastChatMsg = null;
	List<ChatLine> lines;
	Set<Character> failure = new HashSet<>();
	Set<Character> success = new HashSet<>();
	Map<Integer, String> sent = new HashMap<>();

	public UTFSendTest() {
		state(-1);
	}

	private void state(final int state) {
		this.state = state;
		this.timer.reset();
		this.timer.resume();
	}

	@SubscribeEvent
	public void tick(final ClientTickEvent ev) {
		Timer.tick();
		if (this.lines==null)
			this.lines = ReflectionHelper.getPrivateValue(GuiNewChat.class,
					CompatMinecraft.getMinecraft().getMinecraftObj().ingameGUI.getChatGUI(), "field_146252_h", "chatLines");
		{
			String chatMsg = null;
			if (!this.lines.isEmpty())
				chatMsg = this.lines.get(0).getChatComponent().getUnformattedText();
			if (!StringUtils.equals(chatMsg, this.lastChatMsg)) {
				this.lastChatMsg = chatMsg;
				final int chatState = NumberUtils.toInt(StringUtils.substringBetween(chatMsg, "|[", "]|"), -1);
				final String str1 = StringUtils.substringAfter(chatMsg, "]|");
				final String str2 = StringUtils.substringAfter(this.sent.get(chatState), "]|");
				if (chatState!=-1&&!StringUtils.isEmpty(str1)&&!StringUtils.isEmpty(str2)) {
					Log.log.info("Encoded: "+str2);
					Log.log.info("Returned: "+str1);
					final boolean eq = StringUtils.equals(str1, str2);
					Log.log.info("Result: "+eq);
					this.success.addAll(Lists.charactersOf(str2));
					if (!eq) {
						final String diff = StringUtils.difference(str1, str2);
						final char[] chars = diff.toCharArray();
						for (final Character c : chars)
							this.failure.add(c);
					}
					Log.log.info(
							"----------------------------------------------------------------------------------------------------------------------");
				}
			}
		}
		if (this.timer.getTime()>.1f)
			if (this.state<0&&Keyboard.isKeyDown(Keyboard.KEY_BACKSLASH))
				state(2);
			else if (0<=this.state&&this.state<512) {
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i<128; i++) {
					final int code = i+this.state*128;
					if (Character.isDefined(code)) {
						final char[] chars = Character.toChars(code);
						if (chars.length==1&&chars[0]!='§')
							sb.append(chars);
						else
							for (final char c : chars)
								this.failure.add(c);
					}
				}

				final String send = "|["+this.state+"]|"+sb.toString();
				final String msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(send);
				if (msg.isEmpty())
					return;
				CompatMinecraft.getMinecraft().getMinecraftObj().player.sendChatMessage(msg);
				//CompatMinecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(msg);
				this.sent.put(this.state, msg);
				state(this.state+1);
			} else if (this.state==512) {
				{
					final StringBuilder sb = new StringBuilder();
					for (final Character c : this.failure)
						sb.append(c);
					Log.log.info(sb.toString());
				}
				{
					final StringBuilder sb = new StringBuilder();
					for (final Character c : this.success)
						sb.append(c);
					Log.log.info(sb.toString());
					try {
						FileUtils.writeStringToFile(new File("chars.txt"), sb.toString(), StandardCharsets.UTF_8);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
				state(this.state+1);
			}
	}
}
