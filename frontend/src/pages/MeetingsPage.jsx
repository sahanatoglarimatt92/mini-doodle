import { useEffect, useMemo, useState } from "react";
import api from "../services/api";

function MeetingsPage() {
    const [users, setUsers] = useState([]);
    const [calendars, setCalendars] = useState([]);
    const [availableSlots, setAvailableSlots] = useState([]);
    const [meetings, setMeetings] = useState([]);

    const [form, setForm] = useState({
        title: "",
        description: "",
        organizerId: "",
        timeSlotId: "",
        participantIds: []
    });

    const [rescheduleMeetingId, setRescheduleMeetingId] =
        useState(null);

    const [rescheduleSlots, setRescheduleSlots] =
        useState([]);

    const [newTimeSlotId, setNewTimeSlotId] =
        useState("");

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    const selectedOrganizerCalendar = useMemo(() => {
        return calendars.find(
            (calendar) =>
                Number(calendar.userId) ===
                Number(form.organizerId)
        );
    }, [calendars, form.organizerId]);

    const loadInitialData = async () => {
        try {
            setLoading(true);
            setError("");

            const [
                usersResponse,
                calendarsResponse,
                meetingsResponse
            ] = await Promise.all([
                api.get("/users"),
                api.get("/calendars"),
                api.get("/meetings")
            ]);

            setUsers(usersResponse.data);
            setCalendars(calendarsResponse.data);
            setMeetings(meetingsResponse.data);
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to load meeting data."
            );
        } finally {
            setLoading(false);
        }
    };

    const loadMeetings = async () => {
        try {
            const response = await api.get("/meetings");
            setMeetings(response.data);
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to refresh meetings."
            );
        }
    };

    const loadAvailableSlots = async (calendarId) => {
        if (!calendarId) {
            setAvailableSlots([]);
            return;
        }

        try {
            setError("");

            const response = await api.get(
                `/time-slots/calendar/${calendarId}`
            );

            const freeSlots = response.data.filter(
                (slot) => slot.status === "FREE"
            );

            setAvailableSlots(freeSlots);
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to load available time slots."
            );
        }
    };

    useEffect(() => {
        loadInitialData();
    }, []);

    useEffect(() => {
        if (selectedOrganizerCalendar) {
            loadAvailableSlots(
                selectedOrganizerCalendar.id
            );
        } else {
            setAvailableSlots([]);
        }

        setForm((currentForm) => ({
            ...currentForm,
            timeSlotId: ""
        }));
    }, [selectedOrganizerCalendar]);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((currentForm) => ({
            ...currentForm,
            [name]: value
        }));
    };

    const handleParticipantChange = (userId) => {
        setForm((currentForm) => {
            const participantAlreadySelected =
                currentForm.participantIds.includes(userId);

            return {
                ...currentForm,
                participantIds:
                    participantAlreadySelected
                        ? currentForm.participantIds.filter(
                              (id) => id !== userId
                          )
                        : [
                              ...currentForm.participantIds,
                              userId
                          ]
            };
        });
    };

    const addParticipants = async (
        meetingId,
        participantIds
    ) => {
        if (participantIds.length === 0) {
            return;
        }

        await api.post(
            `/meetings/${meetingId}/participants`,
            {
                participantIds
            }
        );
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!form.organizerId) {
            setError("Select an organizer.");
            return;
        }

        if (!form.timeSlotId) {
            setError("Select a free time slot.");
            return;
        }

        try {
            setSubmitting(true);
            setError("");
            setSuccess("");

            const meetingResponse = await api.post(
                "/meetings",
                {
                    organizerId: Number(
                        form.organizerId
                    ),
                    timeSlotId: Number(
                        form.timeSlotId
                    ),
                    title: form.title,
                    description: form.description
                }
            );

            await addParticipants(
                meetingResponse.data.id,
                form.participantIds
            );

            const organizerId =
                form.organizerId;

            setForm({
                title: "",
                description: "",
                organizerId: "",
                timeSlotId: "",
                participantIds: []
            });

            setAvailableSlots([]);
            setSuccess(
                "Meeting created successfully."
            );

            await loadMeetings();

            const organizerCalendar =
                calendars.find(
                    (calendar) =>
                        Number(calendar.userId) ===
                        Number(organizerId)
                );

            if (organizerCalendar) {
                await loadAvailableSlots(
                    organizerCalendar.id
                );
            }
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to create meeting."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async (meetingId) => {
        const confirmed = window.confirm(
            "Are you sure you want to cancel this meeting?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setError("");
            setSuccess("");

            await api.delete(
                `/meetings/${meetingId}`
            );

            setSuccess(
                "Meeting cancelled successfully."
            );

            if (
                rescheduleMeetingId === meetingId
            ) {
                closeReschedule();
            }

            await loadMeetings();

            if (selectedOrganizerCalendar) {
                await loadAvailableSlots(
                    selectedOrganizerCalendar.id
                );
            }
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to cancel meeting."
            );
        }
    };

    const startReschedule = async (meeting) => {
        try {
            setError("");
            setSuccess("");

            const organizerCalendar =
                calendars.find(
                    (calendar) =>
                        Number(calendar.userId) ===
                        Number(
                            meeting.organizerId
                        )
                );

            if (!organizerCalendar) {
                setError(
                    "Organizer calendar not found."
                );
                return;
            }

            const response = await api.get(
                `/time-slots/calendar/${organizerCalendar.id}`
            );

            const freeSlots =
                response.data.filter(
                    (slot) =>
                        slot.status === "FREE"
                );

            setRescheduleMeetingId(
                meeting.id
            );

            setRescheduleSlots(freeSlots);
            setNewTimeSlotId("");
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to load slots for rescheduling."
            );
        }
    };

    const handleReschedule = async (
        meetingId
    ) => {
        if (!newTimeSlotId) {
            setError(
                "Select a new time slot."
            );
            return;
        }

        try {
            setSubmitting(true);
            setError("");
            setSuccess("");

            await api.put(
                `/meetings/${meetingId}/reschedule`,
                {
                    newTimeSlotId: Number(newTimeSlotId)
                }
            );

            closeReschedule();

            setSuccess(
                "Meeting rescheduled successfully."
            );

            await loadMeetings();

            if (selectedOrganizerCalendar) {
                await loadAvailableSlots(
                    selectedOrganizerCalendar.id
                );
            }
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                    "Unable to reschedule meeting."
            );
        } finally {
            setSubmitting(false);
        }
    };

    const closeReschedule = () => {
        setRescheduleMeetingId(null);
        setRescheduleSlots([]);
        setNewTimeSlotId("");
    };

    const formatDate = (value) => {
        if (!value) {
            return "";
        }

        return new Date(
            value
        ).toLocaleDateString();
    };

    const formatTime = (value) => {
        if (!value) {
            return "";
        }

        return new Date(
            value
        ).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const availableParticipants =
        users.filter(
            (user) =>
                Number(user.id) !==
                Number(form.organizerId)
        );

    return (
        <section>
            <h2>Meetings</h2>

            <form
                className="card form"
                onSubmit={handleSubmit}
            >
                <h3>Create Meeting</h3>

                <label>
                    Title
                    <input
                        type="text"
                        name="title"
                        value={form.title}
                        onChange={handleChange}
                        required
                    />
                </label>

                <label>
                    Description
                    <textarea
                        name="description"
                        value={
                            form.description
                        }
                        onChange={
                            handleChange
                        }
                        rows="3"
                    />
                </label>

                <label>
                    Organizer
                    <select
                        name="organizerId"
                        value={
                            form.organizerId
                        }
                        onChange={
                            handleChange
                        }
                        required
                    >
                        <option value="">
                            Select organizer
                        </option>

                        {users.map(
                            (user) => (
                                <option
                                    key={
                                        user.id
                                    }
                                    value={
                                        user.id
                                    }
                                >
                                    {
                                        user.name
                                    }
                                </option>
                            )
                        )}
                    </select>
                </label>

                <label>
                    Available time slot
                    <select
                        name="timeSlotId"
                        value={
                            form.timeSlotId
                        }
                        onChange={
                            handleChange
                        }
                        required
                        disabled={
                            !form.organizerId
                        }
                    >
                        <option value="">
                            Select time slot
                        </option>

                        {availableSlots.map(
                            (slot) => (
                                <option
                                    key={
                                        slot.id
                                    }
                                    value={
                                        slot.id
                                    }
                                >
                                    {formatDate(
                                        slot.startTime
                                    )}{" "}
                                    {formatTime(
                                        slot.startTime
                                    )}{" "}
                                    -{" "}
                                    {formatTime(
                                        slot.endTime
                                    )}
                                </option>
                            )
                        )}
                    </select>
                </label>

                {form.organizerId &&
                    availableSlots.length ===
                        0 && (
                        <p>
                            No free time slots
                            available for this
                            organizer.
                        </p>
                    )}

                <fieldset className="participants-fieldset">
                    <legend>
                        Participants
                    </legend>

                    {availableParticipants.length ===
                    0 ? (
                        <p>
                            No participants
                            available.
                        </p>
                    ) : (
                        availableParticipants.map(
                            (user) => (
                                <label
                                    className="checkbox-label"
                                    key={
                                        user.id
                                    }
                                >
                                    <input
                                        type="checkbox"
                                        checked={form.participantIds.includes(
                                            user.id
                                        )}
                                        onChange={() =>
                                            handleParticipantChange(
                                                user.id
                                            )
                                        }
                                    />

                                    <span>
                                        {
                                            user.name
                                        }{" "}
                                        (
                                        {
                                            user.email
                                        }
                                        )
                                    </span>
                                </label>
                            )
                        )
                    )}
                </fieldset>

                <button
                    type="submit"
                    disabled={submitting}
                >
                    {submitting
                        ? "Creating..."
                        : "Create Meeting"}
                </button>
            </form>

            {error && (
                <p className="error">
                    {error}
                </p>
            )}

            {success && (
                <p className="success">
                    {success}
                </p>
            )}

            <h3>Existing Meetings</h3>

            {loading ? (
                <p>Loading meetings...</p>
            ) : meetings.length === 0 ? (
                <p>No meetings found.</p>
            ) : (
                <div className="grid">
                    {meetings.map(
                        (meeting) => (
                            <article
                                className="card meeting-card"
                                key={
                                    meeting.id
                                }
                            >
                                <h3>
                                    {
                                        meeting.title
                                    }
                                </h3>

                                {meeting.description && (
                                    <p>
                                        {
                                            meeting.description
                                        }
                                    </p>
                                )}

                                <p>
                                    <strong>
                                        Organizer:
                                    </strong>{" "}
                                    {
                                        meeting.organizerName
                                    }
                                </p>

                                <p>
                                    <strong>
                                        Date:
                                    </strong>{" "}
                                    {formatDate(
                                        meeting.startTime
                                    )}
                                </p>

                                <p>
                                    <strong>
                                        Time:
                                    </strong>{" "}
                                    {formatTime(
                                        meeting.startTime
                                    )}{" "}
                                    -{" "}
                                    {formatTime(
                                        meeting.endTime
                                    )}
                                </p>

                                <div>
                                    <strong>
                                        Participants:
                                    </strong>

                                    {meeting
                                        .participants
                                        ?.length >
                                    0 ? (
                                        <ul>
                                            {meeting.participants.map(
                                                (
                                                    participant
                                                ) => (
                                                    <li
                                                        key={
                                                            participant.id
                                                        }
                                                    >
                                                        {
                                                            participant.name
                                                        }
                                                    </li>
                                                )
                                            )}
                                        </ul>
                                    ) : (
                                        <p>
                                            No
                                            participants
                                        </p>
                                    )}
                                </div>

                                {rescheduleMeetingId ===
                                    meeting.id && (
                                    <div className="reschedule-section">
                                        <label>
                                            New time slot

                                            <select
                                                value={
                                                    newTimeSlotId
                                                }
                                                onChange={(
                                                    event
                                                ) =>
                                                    setNewTimeSlotId(
                                                        event
                                                            .target
                                                            .value
                                                    )
                                                }
                                            >
                                                <option value="">
                                                    Select
                                                    new time
                                                    slot
                                                </option>

                                                {rescheduleSlots.map(
                                                    (
                                                        slot
                                                    ) => (
                                                        <option
                                                            key={
                                                                slot.id
                                                            }
                                                            value={
                                                                slot.id
                                                            }
                                                        >
                                                            {formatDate(
                                                                slot.startTime
                                                            )}{" "}
                                                            {formatTime(
                                                                slot.startTime
                                                            )}{" "}
                                                            -{" "}
                                                            {formatTime(
                                                                slot.endTime
                                                            )}
                                                        </option>
                                                    )
                                                )}
                                            </select>
                                        </label>

                                        {rescheduleSlots.length ===
                                            0 && (
                                            <p>
                                                No free
                                                time slots
                                                available.
                                            </p>
                                        )}

                                        <div className="meeting-actions">
                                            <button
                                                type="button"
                                                disabled={
                                                    !newTimeSlotId ||
                                                    submitting
                                                }
                                                onClick={() =>
                                                    handleReschedule(
                                                        meeting.id
                                                    )
                                                }
                                            >
                                                Confirm
                                                Reschedule
                                            </button>

                                            <button
                                                type="button"
                                                className="secondary-button"
                                                onClick={
                                                    closeReschedule
                                                }
                                            >
                                                Close
                                            </button>
                                        </div>
                                    </div>
                                )}

                                <div className="meeting-actions">
                                    <button
                                        type="button"
                                        onClick={() =>
                                            startReschedule(
                                                meeting
                                            )
                                        }
                                    >
                                        Reschedule
                                    </button>

                                    <button
                                        type="button"
                                        className="danger-button"
                                        onClick={() =>
                                            handleDelete(
                                                meeting.id
                                            )
                                        }
                                    >
                                        Cancel Meeting
                                    </button>
                                </div>
                            </article>
                        )
                    )}
                </div>
            )}
        </section>
    );
}

export default MeetingsPage;