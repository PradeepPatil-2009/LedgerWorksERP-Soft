import { Navigate } from "react-router-dom";
import { getUsername } from "../api/authToken";

// The JWT is now an unreadable HttpOnly cookie, so we gate on the stored
// identity instead: no username means no session.
function ProtectedRoute({ children }) {
  const username = getUsername();

  if (!username) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
