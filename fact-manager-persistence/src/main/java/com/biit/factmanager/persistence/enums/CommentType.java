package com.biit.factmanager.persistence.enums;

/*-
 * #%L
 * FactManager (Persistence)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

public enum CommentType {

    ATTACHMENT("Attachment", "attachments"),

    COST_OBJECT("CostObject", "cost_objects"),

    MEETING("Meeting", "meetings"),

    TIME_ENTRY("TimeEntry", "time_entries"),

    WORK_PACKAGE("WorkPackage", "work_packages");

    private final String journableType;

    private final String activityType;

    CommentType(String journableType, String activityType) {
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
