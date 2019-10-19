package net.teamfruit.emojicord.compat;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class CompatCommands {

	public static class CompatCommand {
		public static @Nonnull String getCommandName(final ICommand command) {
			return command.getCommandName();
		}
	
		public static @Nullable List<String> getCommandAliases(final ICommand command) {
			return command.getCommandAliases();
		}
	
		public static @Nonnull String getCommandUsage(final ICommand command, final @Nullable ICommandSender sender) {
			return command.getCommandUsage(sender);
		}
	}

	public static class CompatCommandSender {
		public static boolean canCommandSenderUseCommand(final ICommandSender sender, final int level, final String name) {
			return sender.canCommandSenderUseCommand(level, name);
		}
	}

	public static abstract class CompatRootCommand extends CommandBase implements ICommand {
		@Override
		public @Nullable List<String> addTabCompletionOptions(final @Nullable ICommandSender sender, final @Nullable String[] args, final @Nullable BlockPos pos) {
			return addTabCompletionOptionCompat(sender, args);
		}
	
		public @Nullable abstract List<String> addTabCompletionOptionCompat(final @Nullable ICommandSender sender, final @Nullable String[] args);
	
		@Override
		public @Nonnull String getCommandName() {
			return getCommandNameCompat();
		}
	
		public abstract @Nonnull String getCommandNameCompat();
	
		@Override
		public @Nullable List<String> getCommandAliases() {
			return getCommandAliasesCompat();
		}
	
		public abstract @Nullable List<String> getCommandAliasesCompat();
	
		@Override
		public @Nonnull String getCommandUsage(final @Nullable ICommandSender sender) {
			return getCommandUsageCompat(sender);
		}
	
		public abstract @Nonnull String getCommandUsageCompat(final @Nullable ICommandSender sender);
	
		@Override
		public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
			processCommandCompat(sender, args);
		}
	
		public abstract void processCommandCompat(final @Nullable ICommandSender sender, final @Nullable String[] args) throws CommandException;
	}

	public static abstract class CompatSubCommand implements ICommand {
		@Override
		public @Nullable List<String> addTabCompletionOptions(final @Nullable ICommandSender sender, final @Nullable String[] args, final @Nullable BlockPos pos) {
			return addTabCompletionOptionCompat(sender, args);
		}
	
		public @Nullable abstract List<String> addTabCompletionOptionCompat(final @Nullable ICommandSender sender, final @Nullable String[] args);
	
		@Override
		public @Nonnull String getCommandName() {
			return getCommandNameCompat();
		}
	
		public abstract @Nonnull String getCommandNameCompat();
	
		@Override
		public @Nullable List<String> getCommandAliases() {
			return getCommandAliasesCompat();
		}
	
		public abstract @Nullable List<String> getCommandAliasesCompat();
	
		@Override
		public @Nonnull String getCommandUsage(final @Nullable ICommandSender sender) {
			return getCommandUsageCompat(sender);
		}
	
		public abstract @Nonnull String getCommandUsageCompat(final @Nullable ICommandSender sender);
	
		@Override
		public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
			processCommandCompat(sender, args);
		}
	
		public abstract void processCommandCompat(final @Nullable ICommandSender sender, final @Nullable String[] args) throws CommandException;
	
		@Override
		public boolean canCommandSenderUseCommand(final ICommandSender sender) {
			return canCommandSenderUseCommandCompat(sender);
		}
	
		public abstract boolean canCommandSenderUseCommandCompat(final @Nullable ICommandSender sender);
	
		public abstract int compare(final @Nullable ICommand command);
	
		@Override
		public int compareTo(final @Nullable ICommand command) {
			return compare(command);
		}
	}

	public static class CompatCommandBase {
		public static String buildString(final ICommandSender sender, final String[] args, final int startPos) {
			return CommandBase.buildString(args, startPos);
		}
	}

}
