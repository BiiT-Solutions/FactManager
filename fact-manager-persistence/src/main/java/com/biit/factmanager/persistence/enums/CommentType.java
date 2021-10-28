package com.biit.factmanager.persistence.enums;

public enum CommentType {

	ATTACHMENT("Attachment", "attachments"),

	COST_OBJECT("CostObject", "cost_objects"),

	MEETING("Meeting", "meetings"),

	TIME_ENTRY("TimeEntry", "time_entries"),

	WORK_PACKAGE("WorkPackage", "work_packages");

	private final String journableType;

	private final String activityType;

	private CommentType(String journableType, String activityType) {
		this.journableType = journableType;
		this.activityType = activityType;
	}

	public String getActivityType() {
		return activityType;
	}

	public String getJournableType() {
		return journableType;
	}

}
