CREATE TABLE meetings (
    id BIGSERIAL PRIMARY KEY,

    time_slot_id BIGINT NOT NULL UNIQUE,

    organizer_id BIGINT NOT NULL,

    title VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meetings_time_slot
        FOREIGN KEY (time_slot_id)
        REFERENCES time_slots(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_meetings_organizer
        FOREIGN KEY (organizer_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_meetings_organizer_id
    ON meetings(organizer_id);