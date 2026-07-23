import {
    BrowserRouter,
    Navigate,
    Route,
    Routes
} from "react-router-dom";

import Navbar from "./components/Navbar";
import UsersPage from "./pages/UsersPage";
import TimeSlotsPage from "./pages/TimeSlotsPage";
import MeetingsPage from "./pages/MeetingsPage";
import AvailabilityPage from "./pages/AvailabilityPage";

function App() {
    return (
        <BrowserRouter>
            <Navbar />

            <main className="container">
                <Routes>
                    <Route
                        path="/"
                        element={<Navigate to="/users" replace />}
                    />

                    <Route path="/users" element={<UsersPage />} />
                    <Route path="/time-slots" element={<TimeSlotsPage />} />
                    <Route path="/meetings" element={<MeetingsPage />} />
                    <Route path="/availability" element={<AvailabilityPage />} />
                </Routes>
            </main>
        </BrowserRouter>
    );
}

export default App;