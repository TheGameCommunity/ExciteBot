package com.gamebuster19901.excite.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.apache.http.client.HttpResponseException;

import com.gamebuster19901.excite.Wiimmfi;
import com.gamebuster19901.excite.bot.command.Commands;
import com.gamebuster19901.excite.bot.command.argument.GlobalLiteralArgumentBuilder.GlobalLiteralCommandNode;
import com.gamebuster19901.excite.bot.database.sql.Database;
import com.gamebuster19901.excite.bot.user.ConsoleUser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordBot {

	private static final Logger LOGGER = Logger.getLogger(DiscordBot.class.getName());
	
	private static final List<GatewayIntent> GATEWAYS = Arrays.asList(new GatewayIntent[] {GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.MESSAGE_CONTENT});
	private String botOwner;
	public final JDA jda;
	protected Wiimmfi wiimmfi;
	private boolean dev;
	
	public DiscordBot(Wiimmfi wiimmfi, String botOwner, File secretFile) throws LoginException, IOException {
		this.wiimmfi = wiimmfi;
		BufferedReader reader = null;
		String secret = null;
		
		try {
			reader = new BufferedReader(new FileReader(secretFile));
			secret = reader.readLine();
			JDABuilder builder = JDABuilder.createDefault(secret, GATEWAYS).setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL);
			this.jda = builder.build();
			jda.addEventListener(new EventReceiver());
			dev = Boolean.parseBoolean(reader.readLine());
			System.out.println("DEV: " + dev);
			if(!dev) {
				setupGlobalCommands();
			}
		} 
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e, () -> e.getMessage());
			throw e;
		}
		finally {
			secret = null;
			secretFile = null;
			if(reader != null) {
				reader.close();
			}
		}
		
		secret = null;
		secretFile = null;
	}
	
	public String getOwner() {
		return botOwner;
	}
	
	public void setWiimmfi(Wiimmfi wiimmfi) {
		this.wiimmfi = wiimmfi;
	}
	
	public Wiimmfi getWiimmfi() {
		return wiimmfi;
	}
	
	public void setLoading() {
		Presence presence = jda.getPresence();
		presence.setPresence(OnlineStatus.IDLE, Activity.of(ActivityType.WATCHING, "Loading..."));
	}
	
	public void setNoDB() {
		Presence presence = jda.getPresence();
		presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.STREAMING, "No Database!"));
	}
	
	public void updatePresence() {
		try {
			ConsoleUser user = ConsoleUser.getConsoleUser();
			Database.INSTANCE.isClosed();
		}
		catch(IOError | SQLException e) {
			setNoDB();
			return;
		}
		
		Presence presence = jda.getPresence();
		Throwable e = wiimmfi.getError();
		if(e == null) {
			int playerCount = Wiimmfi.getAcknowledgedPlayerCount();
			if (presence.getStatus() != OnlineStatus.ONLINE || presence.getActivity() == null || presence.getActivity().getType() != ActivityType.WATCHING || !presence.getActivity().getName().equals(playerCount + " racers online")) {
				presence.setPresence(OnlineStatus.ONLINE, Activity.of(ActivityType.WATCHING, playerCount + " racers online"));
			}
		}
		else {
			if(presence.getStatus() != OnlineStatus.DO_NOT_DISTURB) {
				if(e instanceof HttpResponseException) {
					HttpResponseException r = (HttpResponseException) e;
					if(r.getStatusCode() == 503) {
						presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.WATCHING, "Wiimmfi Undergoing Maintenance"));
					}
					else if (r.getStatusCode() >= 500 && r.getStatusCode() < 600) {
						presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.WATCHING, "Wiimmfi offline: " + r.getReasonPhrase()));
					}
				}
				else {
					presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.WATCHING, "Bot offline"));
				}
			}
		}
	}
	
	public SelfUser getSelfUser() {
		return jda.getSelfUser();
	}
	
	private void setupGlobalCommands() {
		List<CommandData> commands = new ArrayList<>();
		Commands.DISPATCHER.getDispatcher().getRoot().getChildren().forEach((command) -> {
			if(!isDev()) {
				if(command instanceof GlobalLiteralCommandNode) {
					SlashCommandData data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(command.getName(), command.getUsageText());
					if(command.getChildren().size() > 0) {
						data.addOption(OptionType.STRING, "arguments", "arguments", true, true);
					}
					System.out.println("Global: " + command.getUsageText());
					commands.add(data);
				}
			}
		});
		this.jda.updateCommands().addCommands(commands).queue();
	}
	
	public boolean isDev() {
		return dev;
	}
	
}
