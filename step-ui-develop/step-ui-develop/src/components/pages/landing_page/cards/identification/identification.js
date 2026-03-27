import React, { useEffect } from "react";
import "./IdentificationPhase.css";
import identification from "../../../../../assets/identification.png";
import { NavbarForP } from "../../navigation_p";
import { Navbar } from "../../navigation";
import { useSelector, useDispatch } from "react-redux";
import { setRole } from "../../../../../redux/actions";
import { decodeToken, getTokenFromCookies } from "../../../../utils/auth";
const IdentificationPhase = () => {
  const dispatch = useDispatch();
  const reduxRole = useSelector((state) => state.practicerating.role);
  useEffect(() => {
    const token = getTokenFromCookies();
    if (!token || typeof token !== "string") {
      console.error("Token is missing or invalid.");
      return;
    }

    try {
      const { role } = decodeToken(token);
      dispatch(setRole(role));
    } catch (error) {
      console.error("Error decoding token:", error.message);
    }
  }, []);
  return (
    <div>
      {reduxRole === "ROLE_SA" ? (
        <Navbar hideContent={true} />
      ) : reduxRole === "ROLE_P" ? (
        <NavbarForP hideContent={true} />
      ) : null}

      <div className="eligibility-container">
        <div className="text-container">
          <h1 className="subtitle1">Eligibility Criteria</h1>
          <ul className="list">
            <li>Be within the A4 to B3 job levels.</li>
            <li>
              Have consistently demonstrated high performance over the last two
              annual review cycles.
            </li>
            <li>
              Received performance ratings of
              <div className="highlight">
                {" "}
                “Significantly Exceeds” or “Exceeds Expectations with
                Accelerated Growth Trajectory”{" "}
              </div>
              as classified in the Talent Insight framework.
            </li>
            <li>
              Exhibit strong potential for future leadership and career
              progression based on their contributions.
            </li>
          </ul>
          <h2 className="subtitle">Key Considerations in Identification</h2>
          <div className="grid">
            <div className="card">
              <p>
                Data from performance reviews and Talent Insight classifications
                are used to shortlist candidates.
              </p>
            </div>
            <div className="card">
              <p>
                This phase does not guarantee selection but ensures that only
                qualified employees move forward to the merit-based selection
                process.
              </p>
            </div>
            <div className="card">
              <p>
                Employees who meet the eligibility criteria are informed and
                encouraged to actively engage in the next phase.
              </p>
            </div>
          </div>

          <p className="description">
            Once this identification process is complete, eligible employees
            proceed to the <span className="highlight">Selection Process</span>,
            where additional weighted attributes are evaluated to finalize the
            merit ranking.
          </p>
        </div>

        <div className="image-wrapper">
          <img
            src={identification}
            alt="Identification Process"
            className="eligibility-image"
          />
        </div>
      </div>
    </div>
    // </div>
  );
};

export default IdentificationPhase;
