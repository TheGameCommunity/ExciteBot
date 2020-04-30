package com.gamebuster19901.excite.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import com.gamebuster19901.excite.Wiimmfi;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

	private static final Logger LOGGER = Logger.getLogger(DiscordBot.class.getName());
	
	private static final List<GatewayIntent> GATEWAYS = Arrays.asList(new GatewayIntent[] {GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS});
	private String botOwner;
	public final JDA jda;
	protected Wiimmfi wiimmfi;
	
	public DiscordBot(Wiimmfi wiimmfi, String botOwner, File secretFile) throws LoginException, IOException {
		this.wiimmfi = wiimmfi;
		BufferedReader reader = null;
		String secret = null;
		
		try {
			reader = new BufferedReader(new FileReader(secretFile));
			secret = reader.readLine();
			secretFile = null;
		} 
		catch (IOException e) {
			secret = null;
			secretFile = null;
			LOGGER.log(Level.SEVERE, e, () -> e.getMessage());
			throw e;
		}
		finally {
			if(reader != null) {
				reader.close();
			}
		}
		
		JDABuilder builder = JDABuilder.create(secret, GATEWAYS).setMemberCachePolicy(MemberCachePolicy.ALL).disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
		this.jda = builder.build();
		jda.addEventListener(new EventReceiver());
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
	
	public void updatePresence() {
		Presence presence = jda.getPresence();
		if(wiimmfi.getError() == null) {
			int playerCount = Wiimmfi.getOnlinePlayers().length;
			if (presence.getStatus() != OnlineStatus.ONLINE || presence.getActivity() == null || presence.getActivity().getType() != ActivityType.WATCHING || !presence.getActivity().getName().equals(playerCount + " racers online")) {
				presence.setPresence(OnlineStatus.ONLINE, Activity.of(ActivityType.WATCHING, playerCount + " racers online"));
				System.out.println("set presence");
			}
		}
		else {
			if(presence.getStatus() != OnlineStatus.DO_NOT_DISTURB || presence.getActivity().getType() != ActivityType.DEFAULT) {
				presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(ActivityType.DEFAULT, "Bot offline"));
				System.out.println("set presence");
			}
		}
	}
	
}
