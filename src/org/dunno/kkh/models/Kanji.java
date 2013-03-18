package org.dunno.kkh.models;


public class Kanji {
	final private int number;
	final private int grade;
	final private String character;
	final private String meaning;
	final private String onReading;
	final private String kunReading;
	
	public Kanji(int number, int grade, String character, String meaning,
			String onReading, String kunReading) {
		super();
		this.number = number;
		this.grade = grade;
		this.character = character;
		this.meaning = meaning;
		this.onReading = onReading;
		this.kunReading = kunReading;
	}

	public int getNumber() {
		return number;
	}

	public int getGrade() {
		return grade;
	}

	public String getCharacter() {
		return character;
	}

	public String getMeaning() {
		return meaning;
	}

	public String getOnReading() {
		return onReading;
	}

	public String getKunReading() {
		return kunReading;
	}
}
