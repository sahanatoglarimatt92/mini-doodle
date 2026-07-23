import { useEffect, useState } from "react";
import api from "../services/api";

function AvailabilityPage() {
    const [users, setUsers] = useState([]);
    const [commonFreeSlots, setCommonFreeSlots] = useState([]);

    const [form, setForm] = useState({
        userIds: [],
        startTime: "",
        endTime: ""
    });

    const [searchRange, setSearchRange] = useState({
        startTime: "",
        endTime: ""
    });

    const [loadingUsers, setLoadingUsers] = useState(true);
    const [searching, setSearching] = useState(false);
    const [hasSearched, setHasSearched] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            setLoadingUsers(true);
            setError("");

            const response = await api.get("/users");

            setUsers(
                Array.isArray(response.data)
                    ? response.data
                    : []
            );
        } catch (exception) {
            console.error("Unable to load users:", exception);

            setError(
                getErrorMessage(
                    exception,
                    "Unable to load users."
                )
            );
        } finally {
            setLoadingUsers(false);
        }
    };

    const handleDateChange = (event) => {
        const { name, value } = event.target;

        setForm((currentForm) => ({
            ...currentForm,
            [name]: value
        }));

        setError("");
        setSuccess("");
    };

    const handleUserSelection = (userId) => {
        setForm((currentForm) => {
            const selected =
                currentForm.userIds.includes(userId);

            return {
                ...currentForm,
                userIds: selected
                    ? currentForm.userIds.filter(
                          (id) => id !== userId
                      )
                    : [...currentForm.userIds, userId]
            };
        });

        setError("");
        setSuccess("");
    };

    const validateForm = () => {
        if (form.userIds.length === 0) {
            setError("Select at least one user.");
            return false;
        }

        if (!form.startTime || !form.endTime) {
            setError(
                "Select both the start time and end time."
            );
            return false;
        }

        const startTime = new Date(form.startTime);
        const endTime = new Date(form.endTime);

        if (Number.isNaN(startTime.getTime())) {
            setError("Start time is invalid.");
            return false;
        }

        if (Number.isNaN(endTime.getTime())) {
            setError("End time is invalid.");
            return false;
        }

        if (endTime <= startTime) {
            setError(
                "End time must be after the start time."
            );
            return false;
        }

        return true;
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!validateForm()) {
            return;
        }

        try {
            setSearching(true);
            setHasSearched(true);
            setError("");
            setSuccess("");
            setCommonFreeSlots([]);

            /*
             * Spring accepts repeated request parameters:
             *
             * userIds=1&userIds=2
             */
            const params = new URLSearchParams();

            form.userIds.forEach((userId) => {
                params.append(
                    "userIds",
                    String(userId)
                );
            });

            params.append(
                "startTime",
                new Date(form.startTime).toISOString()
            );

            params.append(
                "endTime",
                new Date(form.endTime).toISOString()
            );

            const response = await api.get(
                `/availability?${params.toString()}`
            );

            console.log(
                "Full response:",
                JSON.stringify(response.data, null, 2)
            );

            console.log(
                "Common free slots:",
                response.data?.commonFreeSlots
            );

            console.log(
                "Availability response:",
                response.data
            );

            /*
             * AvailabilityResponse:
             *
             * {
             *   searchStartTime,
             *   searchEndTime,
             *   users,
             *   commonFreeSlots
             * }
             */
            const slots = Array.isArray(
                response.data?.commonFreeSlots
            )
                ? response.data.commonFreeSlots
                : [];

            setCommonFreeSlots(slots);

            setSearchRange({
                startTime:
                    response.data?.searchStartTime ??
                    new Date(
                        form.startTime
                    ).toISOString(),

                endTime:
                    response.data?.searchEndTime ??
                    new Date(
                        form.endTime
                    ).toISOString()
            });

            if (slots.length > 0) {
                setSuccess(
                    `${slots.length} common free slot${
                        slots.length === 1 ? "" : "s"
                    } found.`
                );
            }
        } catch (exception) {
            console.error(
                "Unable to find availability:",
                exception
            );

            setCommonFreeSlots([]);

            setError(
                getErrorMessage(
                    exception,
                    "Unable to find availability."
                )
            );
        } finally {
            setSearching(false);
        }
    };

    const clearSearch = () => {
        setForm({
            userIds: [],
            startTime: "",
            endTime: ""
        });

        setSearchRange({
            startTime: "",
            endTime: ""
        });

        setCommonFreeSlots([]);
        setHasSearched(false);
        setError("");
        setSuccess("");
    };

    const selectAllUsers = () => {
        setForm((currentForm) => ({
            ...currentForm,
            userIds: users.map((user) => user.id)
        }));

        setError("");
        setSuccess("");
    };

    const clearSelectedUsers = () => {
        setForm((currentForm) => ({
            ...currentForm,
            userIds: []
        }));

        setError("");
        setSuccess("");
    };

    const formatDate = (value) => {
        if (!value) {
            return "-";
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return "-";
        }

        return date.toLocaleDateString([], {
            year: "numeric",
            month: "short",
            day: "2-digit"
        });
    };

    const formatTime = (value) => {
        if (!value) {
            return "-";
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return "-";
        }

        return date.toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const calculateDuration = (
        startTime,
        endTime
    ) => {
        if (!startTime || !endTime) {
            return 0;
        }

        const start = new Date(startTime);
        const end = new Date(endTime);

        if (
            Number.isNaN(start.getTime()) ||
            Number.isNaN(end.getTime())
        ) {
            return 0;
        }

        return Math.round(
            (end.getTime() - start.getTime()) /
                60000
        );
    };

    const getErrorMessage = (
        exception,
        fallbackMessage
    ) => {
        const responseData =
            exception.response?.data;

        if (typeof responseData === "string") {
            return responseData;
        }

        return (
            responseData?.message ??
            responseData?.detail ??
            responseData?.error ??
            fallbackMessage
        );
    };

    return (
        <section>
            <h2>Availability</h2>

            <p>
                Select users and a time range to find
                their common free slots.
            </p>

            <form
                className="card form"
                onSubmit={handleSubmit}
            >
                <h3>Find Common Availability</h3>

                <label>
                    Start time

                    <input
                        type="datetime-local"
                        name="startTime"
                        value={form.startTime}
                        onChange={handleDateChange}
                        required
                    />
                </label>

                <label>
                    End time

                    <input
                        type="datetime-local"
                        name="endTime"
                        value={form.endTime}
                        onChange={handleDateChange}
                        required
                    />
                </label>

                <fieldset className="participants-fieldset">
                    <legend>Select Users</legend>

                    {loadingUsers ? (
                        <p>Loading users...</p>
                    ) : users.length === 0 ? (
                        <p>
                            No users found. Create users
                            before checking availability.
                        </p>
                    ) : (
                        <>
                            <div className="user-selection-actions">
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={selectAllUsers}
                                    disabled={
                                        form.userIds.length ===
                                        users.length
                                    }
                                >
                                    Select All
                                </button>

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={
                                        clearSelectedUsers
                                    }
                                    disabled={
                                        form.userIds.length ===
                                        0
                                    }
                                >
                                    Clear Selection
                                </button>
                            </div>

                            {users.map((user) => (
                                <label
                                    className="checkbox-label"
                                    key={user.id}
                                >
                                    <input
                                        type="checkbox"
                                        checked={form.userIds.includes(
                                            user.id
                                        )}
                                        onChange={() =>
                                            handleUserSelection(
                                                user.id
                                            )
                                        }
                                    />

                                    <span>
                                        {user.name}{" "}
                                        {user.email
                                            ? `(${user.email})`
                                            : ""}
                                    </span>
                                </label>
                            ))}
                        </>
                    )}
                </fieldset>

                <p className="selection-count">
                    Selected users:{" "}
                    {form.userIds.length}
                </p>

                <div className="availability-actions">
                    <button
                        type="submit"
                        disabled={
                            searching ||
                            loadingUsers ||
                            users.length === 0
                        }
                    >
                        {searching
                            ? "Searching..."
                            : "Find Common Free Slots"}
                    </button>

                    <button
                        type="button"
                        className="secondary-button"
                        onClick={clearSearch}
                        disabled={searching}
                    >
                        Clear
                    </button>
                </div>
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

            {hasSearched &&
                searchRange.startTime &&
                searchRange.endTime && (
                    <div className="card search-summary">
                        <h3>Search Range</h3>

                        <p>
                            <strong>From:</strong>{" "}
                            {formatDate(
                                searchRange.startTime
                            )}{" "}
                            at{" "}
                            {formatTime(
                                searchRange.startTime
                            )}
                        </p>

                        <p>
                            <strong>To:</strong>{" "}
                            {formatDate(
                                searchRange.endTime
                            )}{" "}
                            at{" "}
                            {formatTime(
                                searchRange.endTime
                            )}
                        </p>

                        <p>
                            <strong>
                                Users selected:
                            </strong>{" "}
                            {form.userIds.length}
                        </p>
                    </div>
                )}

            <h3>Common Free Slots</h3>

            {searching ? (
                <p>Searching availability...</p>
            ) : hasSearched &&
              commonFreeSlots.length === 0 &&
              !error ? (
                <div className="card empty-state">
                    <h3>No common free slots found</h3>

                    <p>
                        Try selecting fewer users or a
                        wider search range.
                    </p>
                </div>
            ) : commonFreeSlots.length > 0 ? (
                <div className="grid">
                    {commonFreeSlots.map(
                        (slot, index) => (
                            <article
                                className="card availability-card"
                                key={
                                    slot.id ??
                                    `${slot.startTime}-${slot.endTime}-${index}`
                                }
                            >
                                <span className="status free">
                                    Available
                                </span>

                                <p>
                                    <strong>Date:</strong>{" "}
                                    {formatDate(
                                        slot.startTime
                                    )}
                                </p>

                                <p>
                                    <strong>Start:</strong>{" "}
                                    {formatTime(
                                        slot.startTime
                                    )}
                                </p>

                                <p>
                                    <strong>End:</strong>{" "}
                                    {formatTime(
                                        slot.endTime
                                    )}
                                </p>

                                <p>
                                    <strong>
                                        Duration:
                                    </strong>{" "}
                                    {calculateDuration(
                                        slot.startTime,
                                        slot.endTime
                                    )}{" "}
                                    minutes
                                </p>
                            </article>
                        )
                    )}
                </div>
            ) : (
                <p>
                    Select users and a time range to
                    search for common availability.
                </p>
            )}
        </section>
    );
}

export default AvailabilityPage;