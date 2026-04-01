package com.example.ticketreservationapp;

final class AdminEventListLogic {

    private AdminEventListLogic() {
    }

    static String validateCancelRequest(String adminUserId, String eventId) {
        if (isBlank(adminUserId)) {
            return "Admin access is missing. Open the Admin Portal again.";
        }
        if (!looksLikeUuid(eventId)) {
            return "This demo item is local only. Create a real event to use API actions.";
        }
        return null;
    }

    static String parseErrorMessage(String body) {
        return AdminEventFormLogic.parseErrorMessage(body);
    }

    static boolean looksLikeUuid(String value) {
        return value != null && value.matches("^[0-9a-fA-F\\-]{36}$");
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
