package com.gamebuster19901.excite.bot.command;

import java.util.ArrayList;
import java.util.List;

import com.gamebuster19901.excite.game.Bot;
import com.gamebuster19901.excite.game.Course;
import com.gamebuster19901.excite.game.Cup;
import com.gamebuster19901.excite.game.Mode;
import com.mojang.brigadier.CommandDispatcher;

public class GameDataCommand {

	@SuppressWarnings("rawtypes")
	public static void register(CommandDispatcher<MessageContext> dispatcher) {
		dispatcher.register(Commands.literal("gameData").executes((context) -> {
			return getData(context.getSource());
		}));
	}
	
	@SuppressWarnings("rawtypes")
	public static int getData(MessageContext context) {
		if(context.isOperator()) {
			String message = "";
			
			Bot[] bots2 = Bot.values();
			Cup[] cups2 = Cup.values();
			List<Course> courses2 = new ArrayList<Course>();
			Mode[] modes2 = Mode.values();
			
			String bots = "";
			String cups = "";
			String courses = "";
			String modes = "";
			
			for(Bot bot : bots2) {
				bots = bots + bot.toString() + "\n";
			}
			
			for(Cup cup : cups2) {
				cups = cups + cup.toString() + "\n";
				for(Course course : cup.getCourses()) {
					courses = courses + course.toString() + "\n";
					courses2.add(course);
				}
			}
			
			for(Mode mode : modes2) {
				modes = modes + mode.toString() + "\n";
			}
			
			message = 
				"Modes (" + modes2.length + "):\n\n" +
				modes + "\n\n" + 
				"Bots (" + bots2.length + "):\n\n" +
				bots + "\n\n" + 
				"Cups (" + cups2.length + "):\n\n" +
				cups + "\n\n" +
				"Courses (" + courses2.size() + ")\n\n" +
				courses;
			
			
			context.sendMessage(message);
		}
		else {
			context.sendMessage("You do not have permission to execute this command");
		}
		return 1;
	}
	
}
