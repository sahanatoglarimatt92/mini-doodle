import { NavLink } from "react-router-dom";

function Navbar() {
    return (
        <nav className="navbar">
            <h1>Mini Doodle</h1>

            <div className="nav-links">
                <NavLink to="/users">Users</NavLink>
                <NavLink to="/time-slots">Time Slots</NavLink>
                <NavLink to="/meetings">Meetings</NavLink>
                <NavLink to="/availability">Availability</NavLink>
            </div>
        </nav>
    );
}

export default Navbar;