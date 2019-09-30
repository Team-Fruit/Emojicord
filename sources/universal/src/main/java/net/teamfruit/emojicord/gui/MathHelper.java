package net.teamfruit.emojicord.gui;

public class MathHelper {
	public static int clamp(final int num, final int min, final int max) {
		if (num<min)
			return min;
		else
			return num>max ? max : num;
	}
}
