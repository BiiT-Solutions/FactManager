package com.biit.factmanager.persistence.enums;

public enum EnumerationType {

	TIME_ENTRY_ACTIVITY("TimeEntryActivity"),

	ISSUE_PRIORITY("IssuePriority"),

	DOCUMENT_CATEGORY("DocumentCategory");

	private String code;

	EnumerationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
