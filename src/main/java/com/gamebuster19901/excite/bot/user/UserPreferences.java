package com.gamebuster19901.excite.bot.user;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.audit.ban.Ban;
import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.bot.common.preferences.BooleanPreference;
import com.gamebuster19901.excite.bot.common.preferences.IntegerPreference;
import com.gamebuster19901.excite.bot.common.preferences.LongPreference;
import com.gamebuster19901.excite.bot.common.preferences.StringPreference;
import com.gamebuster19901.excite.output.OutputCSV;
import com.gamebuster19901.excite.util.TimeUtils;

public class UserPreferences implements OutputCSV{
	public static final int DB_VERSION = 2;
	
	private static final String validPasswordChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789,.!?";
	private Random random = new Random();
	
	private StringPreference discord;
	private LongPreference discordId;
	private IntegerPreference notifyThreshold = new IntegerPreference(-1); //-1 means don't notify
	private DurationPreference notifyFrequency = new DurationPreference(Duration.ofMinutes(30), Duration.ofMinutes(5), ChronoUnit.FOREVER.getDuration());
	private ProfilePreference profiles = new ProfilePreference();
	private InstantPreference lastNotification = new InstantPreference(Instant.MIN);
	private BooleanPreference dippedBelowThreshold = new BooleanPreference(true);
	private BooleanPreference notifyContinuously = new BooleanPreference(false);
	private BooleanPreference isAdmin = new BooleanPreference(false);
	private BooleanPreference isOperator = new BooleanPreference(false);
	
	private transient IntegerPreference desiredProfile = new IntegerPreference(-1);
	private transient StringPreference registrationCode = new StringPreference("");
	private transient InstantPreference registrationTimer = new InstantPreference(Instant.MIN);
	private transient IntegerPreference messageCountPastFifteenSeconds = new IntegerPreference(0);

	public UserPreferences(DiscordUser discordUser) {
		if(discordUser instanceof ConsoleUser) {
			discord = new StringPreference("CONSOLE");
		}
		else if(discordUser instanceof UnknownDiscordUser || discordUser.getJDAUser() == null) {
			discord = new StringPreference("UNKNOWN USER");
		}
		else {
			discord = new StringPreference(discordUser.getJDAUser().getAsTag());
		}
		discordId = new LongPreference(discordUser.getId());
	}
	
	public UserPreferences() {
		
	}
	
	public void parsePreferences(String discord, long discordId, int notifyThreshold, Duration notifyFrequency, Player[] profiles, Instant lastNotification, boolean dippedBelowThreshold, boolean notifyContinuously, boolean isAdmin, boolean isOperator) {
		this.discord = new StringPreference(discord);
		this.discordId = new LongPreference(discordId);
		this.notifyThreshold = new IntegerPreference(notifyThreshold);
		this.notifyFrequency = new DurationPreference(notifyFrequency);
		this.profiles = new ProfilePreference(profiles);
		this.lastNotification = new InstantPreference(lastNotification);
		this.dippedBelowThreshold = new BooleanPreference(dippedBelowThreshold);
		this.notifyContinuously = new BooleanPreference(notifyContinuously);
		this.isAdmin = new BooleanPreference(isAdmin);
		this.isOperator = new BooleanPreference(isOperator);
	}
	
	@Override
	public String toCSV() {
		try (
			StringWriter writer = new StringWriter();
			CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withTrim(false));
		)
		{
			printer.printRecord(DB_VERSION, discord, discordId, notifyThreshold, notifyFrequency, profiles, lastNotification, dippedBelowThreshold, notifyContinuously, isAdmin, isOperator);
			printer.flush();
			return writer.toString();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	public String getDiscordTag() {
		return (String) discord.getValue();
	}
	
	public int getNotifyThreshold() {
		return notifyThreshold.getValue();
	}
	
	public Duration getNotifyFrequency() {
		return notifyFrequency.getValue();
	}
	
	public Set<Player> getProfiles() {
		return profiles.getValue();
	}
	
	public Ban[] getBans() {
		return Ban.getBansOfUser(discordId.getValue());
	}
	
	public boolean isBanned() {
		for(Ban ban : getBans()) {
			if (ban.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAdmin() {
		return isOperator.getValue() || isAdmin.getValue();
	}
	
	public boolean isOperator() {
		return isOperator.getValue();
	}
	
	@SuppressWarnings("rawtypes")
	public void setAdmin(MessageContext promoter, boolean admin) {
		isAdmin.setValue(admin);
	}
	
	@SuppressWarnings("rawtypes")
	public void setOperator(MessageContext promoter, boolean operator) {
		isOperator.setValue(operator);
	}
	
	public int getUnpardonedBanCount() {
		int bans = 0;
		for(Ban ban : getBans()) {
			if(!ban.isPardoned()) {
				bans++;
			}
		}
		return bans;
	}
	
	public int getTotalBanCount() {
		return getBans().length;
	}
	
	public void setNotifyThreshold(int threshold) {
		notifyThreshold.setValue(threshold);
	}
	
	public void setNotifyFrequency(Duration duration) {
		notifyFrequency.setValue(duration);
	}
	
	public void setNotifyContinuously(boolean continuous) {
		notifyContinuously.setValue(continuous);
		if(continuous) {
			dippedBelowThreshold.setValue(false);
		}
	}
	
	public void addProfile(int profileID) {
		profiles.addProfile(profileID);
	}
	
	public void addProfile(Player profile) {
		profiles.addProfile(profile);
	}
	
	public void clearRegistration() {
		this.desiredProfile.setValue(-1);
		this.registrationCode.setValue("");
		this.registrationTimer.setValue(Instant.MIN);
	}
	
	public boolean requestingRegistration() {
		return desiredProfile.getValue() != -1;
	}
	
	public String requestRegistration(Player desiredProfile) {
		this.desiredProfile.setValue(desiredProfile.getPlayerID());
		this.registrationCode.setValue(generatePassword());
		this.registrationTimer.setValue(Instant.now().plus(Duration.ofMinutes(5)));
		return (String) registrationCode.getValue();
	}
	
	@SuppressWarnings("rawtypes")
	void sentCommand(MessageContext context) {
		sentCommand(context, 1);
	}
	
	@SuppressWarnings("rawtypes")
	void sentCommand(MessageContext context, int amount) {
		int messageCount = messageCountPastFifteenSeconds.setValue(messageCountPastFifteenSeconds.getValue() + amount);
		if(messageCount >= 5 && messageCount <= 7) {
			DiscordUser user = DiscordUser.getDiscordUser(this.discordId.getValue());
			user.sendMessage(context, user.getJDAUser().getAsMention() + " Slow down! Spamming the bot will result in loss of privilages. (" + messageCount + ")");
		}
		else if(messageCount > 7) {
			Duration banTime;
			switch(getUnpardonedBanCount()) {
				case 0:
					banTime = Duration.ofSeconds(30);
					break;
				case 1:
					banTime = Duration.ofMinutes(5);
					break;
				case 2:
					banTime = Duration.ofMinutes(30);
					break;
				case 3:
					banTime = Duration.ofDays(1);
					break;
				case 4:
					banTime = Duration.ofDays(7);
					break;
				case 5:
					banTime = Duration.ofDays(30);
				default:
					banTime = ChronoUnit.FOREVER.getDuration();
					break;
			}
			DiscordUser.getDiscordUserIncludingUnknown(discordId.getValue()).ban(new MessageContext(), banTime, "Do not spam the bot. You have been banned from using the bot for " + TimeUtils.readableDuration(banTime));
		}
	}
	
	void updateWarningCooldown() {
		if(messageCountPastFifteenSeconds.getValue() > 0) {
			messageCountPastFifteenSeconds.setValue(0);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void updateCooldowns() {
		if(!isBanned()) {
			int playerCount = Wiimmfi.getAcknowledgedPlayerCount();
			int threshold = notifyThreshold.getValue();
			if(threshold == -1) {
				return;
			}
			if(playerCount < threshold) {
				dippedBelowThreshold.setValue(true);
				return;
			}
			else {
				if(lastNotification.getValue().plus(notifyFrequency.getValue()).isBefore(Instant.now())) {
					if(dippedBelowThreshold.getValue()) {
						if(!notifyContinuously.getValue()) {
							dippedBelowThreshold.setValue(false);
						}
						long discordId = this.discordId.getValue();
						DiscordUser user = DiscordUser.getDiscordUser(discordId);
						if(user != null) {
							for(Player player : Wiimmfi.getOnlinePlayers()) {
								if(player.getDiscord() == discordId) {
									return;
								}
							}
							user.sendMessage(user.getJDAUser().getAsMention() + ", there are " + playerCount + " players online!\n\n" +
								Wiimmfi.getOnlinePlayerList(new MessageContext(user), false)
							);
							lastNotification.setValue(Instant.now());
						}
					}
				}
			}
		}
	}
	
	private void register() {
		DiscordUser user = DiscordUser.getDiscordUser(this.discordId.getValue());
		Player desiredPlayer = Player.getPlayerByID(desiredProfile.getValue());
		user.sendMessage(user.getJDAUser().getAsMention() + "You have successfully registered the following profile:\n\n" + desiredPlayer + "\n\nYou may change it's name back to what it was before.");
		this.profiles.addProfile(desiredProfile.getValue());
		desiredPlayer.setDiscord(user.getId());
		if(this.getProfiles().contains(desiredPlayer)) {
			clearRegistration();
		}
		else {
			throw new AssertionError();
		}
	}
	
	private final String generatePassword() {
		char[] sequence = new char[7];
		for(int i = 0; i < sequence.length; i++) {
			sequence[i] = validPasswordChars.charAt(random.nextInt(validPasswordChars.length()));
		}
		return new String(sequence);
	}
	
	public static void attemptRegister() {
		for(DiscordUser user : DiscordUser.getKnownUsers()) {
			if(!(user instanceof UnloadedDiscordUser)) {
				UserPreferences preferences = user.preferences;
				if(preferences.requestingRegistration()) {
					int desiredProfile = preferences.desiredProfile.getValue();
					if(desiredProfile > -1) {
						if(preferences.registrationTimer.getValue().isAfter(Instant.now())) {
							for(Player player : Wiimmfi.getOnlinePlayers()) {
								if(player.getName().equals(preferences.registrationCode.getValue().toString())) {
									if(player.getPlayerID() == desiredProfile) {
										preferences.register();
									}
									else {
										user.sendMessage("Registration aborted:\n\nYou selected the following account ID for registration:\n`" + desiredProfile + "`\nbut logged in with\n`" + player.getPlayerID() + "`");
										preferences.clearRegistration();
									}
								}
							}
						}
						else {
							user.sendMessage("Registration for \n" + Player.getPlayerByID(desiredProfile).toString() + "\n has expired!");
							preferences.clearRegistration();
						}
					}
					else {
						throw new IllegalStateException("No profile to register!");
					}
				}
			}
		}
	}
	
}
