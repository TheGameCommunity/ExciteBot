package com.gamebuster19901.excite.bot;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.Player;
import com.gamebuster19901.excite.Wiimmfi;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerMessageReceivedEvent extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String message = e.getMessage().getContentRaw();
		Wiimmfi wiimmfi = Main.discordBot.getWiimmfi();
		
		if(e.getGuild().getSelfMember().hasPermission(e.getChannel(), Permission.MESSAGE_WRITE)) {
			
			if(message.equals("!online")) {
				if(checkNotErrored(e)) {
					Player[] players = wiimmfi.getOnlinePlayers();
					
					String response = "Players Online: (" + wiimmfi.getOnlinePlayers().length + ")" + "\n\n";
					for(int i = 0; i < players.length && i < 24; i++) {
						response += players[i].toString() + '\n';
						if (i == 23 && players.length > 24) {
							response += "and (" + (players.length - 24) + ") others";
						}
					}
					e.getChannel().sendMessage(response).complete();
				}
				else {
					e.getChannel().sendMessage("Bot offline due to an error: " + wiimmfi.getError().getClass().getCanonicalName() + ": " + wiimmfi.getError().getMessage());
				}
			}
			
			else if (message.startsWith("!whois ")) {
				if(checkNotErrored(e)) {
					Player[] players = Wiimmfi.getKnownPlayers();
					String lookingFor = message.substring(7);
					String response = "";
					
					for(Player p : players) {
						if(p.getName().equalsIgnoreCase(lookingFor)) {
							response = response + p + "\n";
						}
						else if (p.getDiscord().toLowerCase().startsWith(lookingFor.toLowerCase())) {
							response = response + p + "\n";
						}
						else if (p.getFriendCode().equals(lookingFor)) {
							response = response + p + "\n";
						}
						else if (lookingFor.equals("" + p.getPlayerID())) {
							response = response + p + "\n";
						}
					}
					
					if(response.isEmpty()) {
						response = "No players found.";
					}
					
					e.getChannel().sendMessage(response).complete();
				}
			}
		}
		else {
			System.out.println("Insufficient permission");
		}
	}
	
	private boolean checkNotErrored(GuildMessageReceivedEvent e) {
		e.getChannel().sendTyping().complete();
		Throwable error = Main.discordBot.getWiimmfi().getError();
		if(error != null) {
			e.getChannel().sendMessage("Could not retrieve data: " + error.getClass().getCanonicalName() + ":" + error.getMessage());
			return false;
		}
		return true;
	}
	
}
