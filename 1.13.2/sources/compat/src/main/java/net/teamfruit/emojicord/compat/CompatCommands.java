package net.teamfruit.emojicord.compat;

public class CompatCommands {
	/*
	public static class CompatCommand {
		public static @Nonnull String getCommandName(final ICommand command) {
			return command.getName();
		}
	
		public static @Nullable List<String> getCommandAliases(final ICommand command) {
			return command.getAliases();
		}
	
		public static @Nonnull String getCommandUsage(final ICommand command, final @Nullable ICommandSender sender) {
			return command.getUsage(sender);
		}
	}
	
	public static class CompatCommandSender {
		public static boolean canCommandSenderUseCommand(final ICommandSender sender, final int level, final String name) {
			return sender.canUseCommand(level, name);
		}
	}
	
	public static abstract class CompatRootCommand extends CommandBase implements ICommand {
		@Override
		public @Nullable List<String> getTabCompletions(final MinecraftServer server, final @Nullable ICommandSender sender, final @Nullable String[] args, final @Nullable BlockPos pos) {
			return addTabCompletionOptionCompat(sender, args);
		}
	
		public @Nullable abstract List<String> addTabCompletionOptionCompat(final @Nullable ICommandSender sender, final @Nullable String[] args);
	
		@Override
		public @Nonnull String getName() {
			return getCommandNameCompat();
		}
	
		public abstract @Nonnull String getCommandNameCompat();
	
		@Override
		public @Nullable List<String> getAliases() {
			return getCommandAliasesCompat();
		}
	
		public abstract @Nullable List<String> getCommandAliasesCompat();
	
		@Override
		public @Nonnull String getUsage(final @Nullable ICommandSender sender) {
			return getCommandUsageCompat(sender);
		}
	
		public abstract @Nonnull String getCommandUsageCompat(final @Nullable ICommandSender sender);
	
		@Override
		public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
			processCommandCompat(sender, args);
		}
	
		public abstract void processCommandCompat(final @Nullable ICommandSender sender, final @Nullable String[] args) throws CommandException;
	}
	
	public static abstract class CompatSubCommand implements ICommand {
		@Override
		public @Nullable List<String> getTabCompletions(final MinecraftServer server, final @Nullable ICommandSender sender, final @Nullable String[] args, final @Nullable BlockPos pos) {
			return addTabCompletionOptionCompat(sender, args);
		}
	
		public @Nullable abstract List<String> addTabCompletionOptionCompat(final @Nullable ICommandSender sender, final @Nullable String[] args);
	
		@Override
		public @Nonnull String getName() {
			return getCommandNameCompat();
		}
	
		public abstract @Nonnull String getCommandNameCompat();
	
		@Override
		public @Nullable List<String> getAliases() {
			return getCommandAliasesCompat();
		}
	
		public abstract @Nullable List<String> getCommandAliasesCompat();
	
		@Override
		public @Nonnull String getUsage(final @Nullable ICommandSender sender) {
			return getCommandUsageCompat(sender);
		}
	
		public abstract @Nonnull String getCommandUsageCompat(final @Nullable ICommandSender sender);
	
		@Override
		public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
			processCommandCompat(sender, args);
		}
	
		public abstract void processCommandCompat(final @Nullable ICommandSender sender, final @Nullable String[] args) throws CommandException;
	
		@Override
		public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
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
	*/
}
