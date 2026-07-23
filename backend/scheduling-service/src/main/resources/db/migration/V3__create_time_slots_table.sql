CREATE TABLE time_slots (
    id BIGSERIAL PRIMARY KEY,

    calendar_id BIGINT NOT NULL,

    start_time TIMESTAMPTZ NOT NULL,

    end_time TIMESTAMPTZ NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'FREE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_time_slots_calendar
        FOREIGN KEY (calendar_id)
        REFERENCES calendars(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_time_slot_time_range
        CHECK (end_time > start_time),

    CONSTRAINT chk_time_slot_status
        CHECK (status IN ('FREE', 'BOOKED'))
);

CREATE INDEX idx_time_slots_calendar_start_time
    ON time_slots(calendar_id, start_time);

CREATE INDEX idx_time_slots_calendar_status
    ON time_slots(calendar_id, status);