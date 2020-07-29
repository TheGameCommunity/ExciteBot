package com.gamebuster19901.excite.game;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.WordUtils;

public enum Cup {
	
	TRAINING(new Mode[] {Mode.EXCITE, Mode.TRAINING}){{
		this.courses = new Course[] {
			new Course(this, "Drive"),
			new Course(this, "Turbo"),
			new Course(this, "Jump"),
			new Course(this, "Turbo Jump"),
			new Course(this, "Air Spin"),
		};
	}},
	
	SCHOOL(Mode.NORMAL){{
		this.courses = new Course[] {
			new Course(this, "Mexico"),
			new Course(this, "Fiji"),
			new Course(this, "Canada"),
			new Course(this, "Finland")
		};
	}},
	
	BRONZE(Mode.NORMAL){{
		this.courses = new Course[] {
			new Course(this, "Egypt"),
			new Course(this, "Scotland"),
			new Course(this, "Mexico"),
			new Course(this, "China"),
			new Course(this, "Kilimanjaro")
		};
	}},
	
	SILVER(Mode.NORMAL){{
		this.courses = new Course[] {
			new Course(this, "Mexico"),
			new Course(this, "China"),
			new Course(this, "Kilimanjaro"),
			new Course(this, "Finland"),
			new Course(this, "Egypt")
		};
	}},
	
	GOLD(Mode.NORMAL){{
		this.courses = new Course[] {
			new Course(this, "Tasmania"),
			new Course(this, "Fiji"),
			new Course(this, "Scotland"),
			new Course(this, "Mexico"),
			new Course(this, "Guatemala")
		};
	}},
	
	PLATINUM(Mode.NORMAL){{
		this.courses = new Course[] {
			new Course(this, "China"),
			new Course(this, "Fiji"),
			new Course(this, "Canada"),
			new Course(this, "Guatemala"),
			new Course(this, "Tasmania")
		};
	}},
	
	CRYSTAL(new Mode[] {Mode.SUPER_EXCITE, Mode.MIRROR_EXCITE, Mode.VERSUS, Mode.WIFI}){{
		this.courses = new Course[] {
			new Course(this, "Nebula")	
		};
	}},
	
	POKER_TRAINING(Mode.POKER, Mode.TRAINING){{
		this.courses = new Course[] {
			new Course(this, "Poker Basics 1"),
			new Course(this, "Poker Basics 2"),
			new Course(this, "Next Card Set"),
			new Course(this, "New Cards"),
			new Course(this, "Making Hands")
		};
	}},
	
	POKER(Mode.POKER, Mode.WIFI){{
		this.courses = new Course[] {
			new Course(this, "Kilimanjaro"),
			new Course(this, "Tasmania"),
			new Course(this, "Fiji")
		};
	}},
	
	MINIGAME(Mode.MINIGAME, Mode.VERSUS){{
		this.courses = new Course[] {
			new Course(this, "Soccer"),
			new Course(this, "Bowling"),
			new Course(this, "Red Bar"),
			new Course(this, "Bar Challenge"),
			new Course(this, "Song Rail"),
			new Course(this, "Dart"),
			new Course(this, "Hand Escape"),
			new Course(this, "Sports"),
			new Course(this, "Glider"),
			new Course(this, "Leg Challenge")
		};
	}},
	
	OTHER();
	
	protected Course[] courses = new Course[] {};
	
	private final List<Mode> modes;
	
	private Cup(Mode...modes) {
		this.modes = Arrays.asList(modes);
	}
	
	public boolean isTutorial() {
		return modes.contains(Mode.TRAINING);
	}
	
	public boolean isOnline() {
		return modes.contains(Mode.WIFI);
	}
	
	public boolean isMode(Mode mode) {
		return modes.contains(mode);
	}
	
	@Override
	public String toString() {
		return WordUtils.capitalizeFully(this.name().replaceAll("_", " "));
	}
	
	public static Cup fromString(String cupString) {
		for(Cup cup : values()) {
			if(cupString.toLowerCase().contains(cup.toString().toLowerCase())) {
				return cup;
			}
		}
		throw new IllegalArgumentException("Could not derive cup from string: " + cupString);
	}
}
