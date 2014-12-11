package com.example.openfirechat.model;

public class Emotion {
	private String phrase;
	private String imageName;

	public Emotion(String phrase, String imageName) {
		super();
		this.phrase = phrase;
		this.imageName = imageName;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
