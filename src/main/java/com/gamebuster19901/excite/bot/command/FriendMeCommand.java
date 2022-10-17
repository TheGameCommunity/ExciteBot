package com.gamebuster19901.excite.bot.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.gamebuster19901.excite.Main;
import com.gamebuster19901.excite.bot.mail.AddFriendResponse;
import com.gamebuster19901.excite.bot.mail.Mailbox;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.mojang.brigadier.CommandDispatcher;

public class FriendMeCommand {

	public static void register(CommandDispatcher<MessageContext>  dispatcher) {
		dispatcher.register(Commands.literal("friendMe").executes((context) -> {return sendFriendRequest(context.getSource());}));
		dispatcher.register(Commands.literal("friendme").executes((context) -> {return sendFriendRequest(context.getSource());}));
	}

	private static int sendFriendRequest(MessageContext context) {
		if(context.isDiscordContext()) {
			HashSet<AddFriendResponse> friendRequests = new HashSet<>();
			HashMap<Wii, Exception> errs = new HashMap<>();
			Wii[] wiis = context.getDiscordAuthor().getRegisteredWiis();
			
			int i = 0;
			for(; i < wiis.length; i++) {
				try {
					Wii wii = wiis[i];
					if(wii.equals(Mailbox.ADDRESS)) {
						throw new IllegalArgumentException("This wii belongs to " + Main.discordBot.getSelfUser().getAsMention() + "! (Main mailbox)");
					}
					else if (wii.getOwner().getID() == Main.discordBot.getSelfUser().getIdLong()) {
						throw new IllegalArgumentException("This wii belongs to " + Main.discordBot.getSelfUser().getAsMention() + "!");
					}
					friendRequests.add(new AddFriendResponse(wiis[i]));
				} catch (Exception e) {
					errs.put(wiis[i], e);
				}
			}
			if(i == 0) {
				context.sendMessage("You have not registered any wiis.");
				return 0;
			}
			if(errs.size() > 0) {
				for(Entry<Wii, Exception> entry: errs.entrySet()) {
					context.sendMessage("Failed to send friend request to: " + entry.getKey().getWiiCode() + " \n\n" + StacktraceUtil.getStackTrace(entry.getValue()));
				}
				context.sendMessage("Failed to send " + errs.size() + " friend request(s):");
			}
			context.sendMessage("Successfully sent " + friendRequests.size() + "/" + wiis.length + " friend request(s)");
			return friendRequests.size();
		}
		else {
			context.sendMessage("This command must be executed via Discord.");
			return 0;
		}
	}
	
}
