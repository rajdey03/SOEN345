package com.example.ticketreservationapp;

import org.json.JSONObject;

final class AdminEventFormLogic {

    private AdminEventFormLogic() {
    }

    static String validate(AdminEventDraft draft) {
        if (isBlank(draft.getOrganizerId())) return "Organizer ID is required";
        if (isBlank(draft.getTitle())) return "Event title is required";
        if (isBlank(draft.getDescription())) return "Description is required";
        if (isBlank(draft.getCategory())) return "Category is required";
        if (isBlank(draft.getLocation())) return "Location is required";
        if (isBlank(draft.getEventDate())) return "Event date is required";
        if (isBlank(draft.getStartTime())) return "Start time is required";
        if (isBlank(draft.getEndTime())) return "End time is required";
        return draft.getTotalCapacity() > 0 ? null : "Total capacity is required";
    }

    static AdminEvent buildEventFromResponse(String responseBody, String fallbackEventId, AdminEventDraft fallbackDraft) {
        try {
            JSONObject json = new JSONObject(responseBody);
            return new AdminEvent(
                    json.optString("event_id", fallbackEventId),
                    json.optString("organizer_id", fallbackDraft.getOrganizerId()),
                    json.optString("title", fallbackDraft.getTitle()),
                    json.optString("description", fallbackDraft.getDescription()),
                    json.optString("category", fallbackDraft.getCategory()),
                    json.optString("location", fallbackDraft.getLocation()),
                    json.optString("event_date", fallbackDraft.getEventDate()),
                    json.optString("start_time", fallbackDraft.getStartTime()),
                    json.optString("end_time", fallbackDraft.getEndTime()),
                    json.optInt("total_capacity", fallbackDraft.getTotalCapacity()),
                    json.optString("status", fallbackDraft.getStatus())
            );
        } catch (Exception ignored) {
            return new AdminEvent(
                    fallbackEventId,
                    fallbackDraft.getOrganizerId(),
                    fallbackDraft.getTitle(),
                    fallbackDraft.getDescription(),
                    fallbackDraft.getCategory(),
                    fallbackDraft.getLocation(),
                    fallbackDraft.getEventDate(),
                    fallbackDraft.getStartTime(),
                    fallbackDraft.getEndTime(),
                    fallbackDraft.getTotalCapacity(),
                    fallbackDraft.getStatus()
            );
        }
    }

    static String parseErrorMessage(String body) {
        try {
            JSONObject json = new JSONObject(body);
            if (json.has("message")) {
                return json.getString("message");
            }
            return body;
        } catch (Exception ignored) {
            return body == null || body.isEmpty() ? "Request failed." : body;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
