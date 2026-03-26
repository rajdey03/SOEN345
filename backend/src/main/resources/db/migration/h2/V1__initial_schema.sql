CREATE TABLE users (
    user_id CHAR(36) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_verified BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone_number ON users (phone_number);
CREATE INDEX idx_users_role ON users (role);

CREATE TABLE organizers (
    organizer_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL UNIQUE,
    organization_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(255),
    CONSTRAINT fk_organizers_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE INDEX idx_organizers_user_id ON organizers (user_id);

CREATE TABLE events (
    event_id CHAR(36) PRIMARY KEY,
    organizer_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    category VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_capacity INTEGER NOT NULL,
    available_capacity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_events_organizer
        FOREIGN KEY (organizer_id) REFERENCES organizers (organizer_id),
    CONSTRAINT chk_events_capacity
        CHECK (total_capacity >= 0 AND available_capacity >= 0 AND available_capacity <= total_capacity)
);

CREATE INDEX idx_events_organizer_id ON events (organizer_id);
CREATE INDEX idx_events_category ON events (category);
CREATE INDEX idx_events_location ON events (location);
CREATE INDEX idx_events_event_date ON events (event_date);
CREATE INDEX idx_events_status ON events (status);

CREATE TABLE reservations (
    reservation_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    event_id CHAR(36) NOT NULL,
    reservation_date TIMESTAMP NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_reservations_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_reservations_event
        FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT chk_reservations_values
        CHECK (quantity > 0 AND total_price >= 0)
);

CREATE INDEX idx_reservations_user_id ON reservations (user_id);
CREATE INDEX idx_reservations_event_id ON reservations (event_id);
CREATE INDEX idx_reservations_status ON reservations (status);
CREATE INDEX idx_reservations_reservation_date ON reservations (reservation_date);

CREATE TABLE tickets (
    ticket_id CHAR(36) PRIMARY KEY,
    reservation_id CHAR(36) NOT NULL,
    event_id CHAR(36) NOT NULL,
    ticket_code VARCHAR(255) NOT NULL UNIQUE,
    seat_number VARCHAR(255),
    ticket_status VARCHAR(50) NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_tickets_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id),
    CONSTRAINT fk_tickets_event
        FOREIGN KEY (event_id) REFERENCES events (event_id)
);

CREATE INDEX idx_tickets_reservation_id ON tickets (reservation_id);
CREATE INDEX idx_tickets_event_id ON tickets (event_id);
CREATE INDEX idx_tickets_status ON tickets (ticket_status);

CREATE TABLE notification_log (
    notification_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    reservation_id CHAR(36),
    channel VARCHAR(50) NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    sent_at TIMESTAMP,
    CONSTRAINT fk_notification_log_user
        FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_notification_log_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservations (reservation_id)
);

CREATE INDEX idx_notification_log_user_id ON notification_log (user_id);
CREATE INDEX idx_notification_log_reservation_id ON notification_log (reservation_id);
CREATE INDEX idx_notification_log_channel ON notification_log (channel);
CREATE INDEX idx_notification_log_message_type ON notification_log (message_type);
