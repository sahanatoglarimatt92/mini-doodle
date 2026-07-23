import { useEffect, useState } from "react";
import api from "../services/api";

function TimeSlotsPage() {
    const [calendars, setCalendars] = useState([]);
    const [selectedCalendarId, setSelectedCalendarId] = useState("");
    const [timeSlots, setTimeSlots] = useState([]);

    const [form, setForm] = useState({
        startTime: "",
        endTime: ""
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const loadCalendars = async () => {
        try {
            const response = await api.get("/calendars");
            setCalendars(response.data);

            if (response.data.length > 0) {
                setSelectedCalendarId(String(response.data[0].id));
            }
        } catch (exception) {
            console.error(exception);
            setError("Unable to load calendars.");
        }
    };

    const loadTimeSlots = async (calendarId) => {
        if (!calendarId) {
            setTimeSlots([]);
            return;
        }

        try {
            setLoading(true);
            setError("");

            const response = await api.get(
                `/time-slots/calendar/${calendarId}`
            );

            setTimeSlots(response.data);
        } catch (exception) {
            console.error(exception);
            setError("Unable to load time slots.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCalendars();
    }, []);

    useEffect(() => {
        if (selectedCalendarId) {
            loadTimeSlots(selectedCalendarId);
        }
    }, [selectedCalendarId]);

    const handleCalendarChange = (event) => {
        setSelectedCalendarId(event.target.value);
        setError("");
        setSuccess("");
    };

    const handleFormChange = (event) => {
        const { name, value } = event.target;

        setForm((currentForm) => ({
            ...currentForm,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!selectedCalendarId) {
            setError("Select a calendar.");
            return;
        }

        try {
            setError("");
            setSuccess("");

            await api.post("/time-slots", {
                calendarId: Number(selectedCalendarId),
                startTime: new Date(form.startTime).toISOString(),
                endTime: new Date(form.endTime).toISOString()
            });

            setForm({
                startTime: "",
                endTime: ""
            });

            setSuccess("Time slot created successfully.");

            await loadTimeSlots(selectedCalendarId);
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                "Unable to create time slot."
            );
        }
    };

    const handleDelete = async (timeSlotId) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this time slot?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setError("");
            setSuccess("");

            await api.delete(`/time-slots/${timeSlotId}`);

            setSuccess("Time slot deleted successfully.");

            await loadTimeSlots(selectedCalendarId);
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                "Unable to delete time slot."
            );
        }
    };

    const formatDateTime = (value) => {
        if (!value) {
            return "";
        }

        return new Date(value).toLocaleString();
    };

    return (
        <section>
            <h2>Time Slots</h2>

            <div className="card form">
                <label>
                    Calendar
                    <select
                        value={selectedCalendarId}
                        onChange={handleCalendarChange}
                    >
                        <option value="">Select calendar</option>

                        {calendars.map((calendar) => (
                            <option
                                key={calendar.id}
                                value={calendar.id}
                            >
                                {calendar.name ??
                                    calendar.userName ??
                                    `Calendar ${calendar.id}`}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            <form className="card form" onSubmit={handleSubmit}>
                <h3>Create Time Slot</h3>

                <label>
                    Start time
                    <input
                        type="datetime-local"
                        name="startTime"
                        value={form.startTime}
                        onChange={handleFormChange}
                        required
                    />
                </label>

                <label>
                    End time
                    <input
                        type="datetime-local"
                        name="endTime"
                        value={form.endTime}
                        onChange={handleFormChange}
                        required
                    />
                </label>

                <button type="submit">Create Time Slot</button>
            </form>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">{success}</p>}

            <h3>Existing Time Slots</h3>

            {loading ? (
                <p>Loading time slots...</p>
            ) : timeSlots.length === 0 ? (
                <p>No time slots found for this calendar.</p>
            ) : (
                <div className="grid">
                    {timeSlots.map((timeSlot) => (
                        <article
                            className="card time-slot-card"
                            key={timeSlot.id}
                        >
                            <div className="card-header">
                                <span
                                    className={
                                        timeSlot.status === "BOOKED"
                                            ? "status booked"
                                            : "status free"
                                    }
                                >
                                    {timeSlot.status}
                                </span>
                            </div>

                            <p>
                                <strong>Start:</strong>{" "}
                                {formatDateTime(timeSlot.startTime)}
                            </p>

                            <p>
                                <strong>End:</strong>{" "}
                                {formatDateTime(timeSlot.endTime)}
                            </p>

                            {timeSlot.status === "FREE" && (
                                <button
                                    type="button"
                                    className="danger-button"
                                    onClick={() =>
                                        handleDelete(timeSlot.id)
                                    }
                                >
                                    Delete
                                </button>
                            )}
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}

export default TimeSlotsPage;