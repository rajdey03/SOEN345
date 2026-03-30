package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AdminEventListLogicTest {

    @Test
    public void validateCancelRequest_requiresAdminAccessFirst() {
        assertEquals(
                "Admin access is missing. Open the Admin Portal again.",
                AdminEventListLogic.validateCancelRequest(null, "319132e6-c2d7-4ccf-bea4-46c4f451e266")
        );
    }

    @Test
    public void validateCancelRequest_rejectsPlaceholderIds() {
        assertEquals(
                "This demo item is local only. Create a real event to use API actions.",
                AdminEventListLogic.validateCancelRequest("319132e6-c2d7-4ccf-bea4-46c4f451e266", "event-001")
        );
    }

    @Test
    public void validateCancelRequest_allowsRealAdminAndRealUuid() {
        assertNull(AdminEventListLogic.validateCancelRequest(
                "319132e6-c2d7-4ccf-bea4-46c4f451e266",
                "9dd1d1a4-a8db-4a85-87bc-8cc6fc3be71a"
        ));
    }

    @Test
    public void looksLikeUuid_acceptsCanonicalUuidShape() {
        assertTrue(AdminEventListLogic.looksLikeUuid("9dd1d1a4-a8db-4a85-87bc-8cc6fc3be71a"));
    }
}
