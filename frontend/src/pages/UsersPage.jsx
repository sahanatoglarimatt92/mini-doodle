import { useEffect, useState } from "react";
import api from "../services/api";

function UsersPage() {
    const [users, setUsers] = useState([]);
    const [form, setForm] = useState({
        name: "",
        email: ""
    });
    const [error, setError] = useState("");

    const loadUsers = async () => {
        try {
            const response = await api.get("/users");
            setUsers(response.data);
            setError("");
        } catch (exception) {
            console.error(exception);
            setError("Unable to load users. Check the backend.");
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((currentForm) => ({
            ...currentForm,
            [name]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            await api.post("/users", form);

            setForm({
                name: "",
                email: ""
            });

            await loadUsers();
        } catch (exception) {
            console.error(exception);

            setError(
                exception.response?.data?.message ??
                "Unable to create user."
            );
        }
    };

    return (
        <section>
            <h2>Users</h2>

            <form className="card form" onSubmit={handleSubmit}>
                <h3>Create User</h3>

                <label>
                    Name
                    <input
                        type="text"
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        required
                    />
                </label>

                <label>
                    Email
                    <input
                        type="email"
                        name="email"
                        value={form.email}
                        onChange={handleChange}
                        required
                    />
                </label>

                <button type="submit">Create User</button>
            </form>

            {error && <p className="error">{error}</p>}

            <h3>Existing Users</h3>

            <div className="grid">
                {users.map((user) => (
                    <article className="card" key={user.id}>
                        <h3>{user.name}</h3>
                        <p>{user.email}</p>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default UsersPage;