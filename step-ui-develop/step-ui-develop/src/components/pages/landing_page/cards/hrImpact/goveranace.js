import React, { useEffect } from "react";
import "./GovernanceMetrics.css";
import { NavbarForP } from "../../navigation_p";
import { Navbar } from "../../navigation";
import { useSelector, useDispatch } from "react-redux";
import { setRole } from "../../../../../redux/actions";
import { decodeToken, getTokenFromCookies } from "../../../../utils/auth";
const GovernanceMetrics = () => {
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

      <h2 className="subtitle">STEP Core Team Composition</h2>
      <div className="grid">
        <div className="card">
          <p>
            <h3> Practices (Competency Heads)</h3>
            <br></br> Define future skills and assess participants' potential.
          </p>
        </div>
        <div className="card">
          <p>
            <h3>Delivery</h3>
            <br></br>
            Align STEP objectives with project and business needs.
          </p>
        </div>
        <div className="card">
          <p>
            <h3>Total Rewards</h3>
            <br></br>
            Ensure program alignment with performance and recognition
            frameworks.
          </p>
        </div>
        <div className="card">
          <p>
            <h3>HR</h3>
            <br></br> Drive program execution, engagement, and role readiness
            tracking.
          </p>
        </div>
        <div className="card">
          <p>
            <h3>Sponsorship from Delivery Leadership</h3>
            <br></br> Provide strategic direction and endorsement.
          </p>
        </div>
      </div>

      <h2 className="subtitle">Governance & Tracking Mechanisms</h2>
      <div className="three-column">
        <div>
          <h3>Monthly Role Readiness Index (RRI) Reports</h3>
          <ul className="list">
            <li>
              Regular tracking of participants' skill development progress.
            </li>
            <li>Insights shared on key improvements and focus areas.</li>
            <li>Transparent progress updates to all stakeholders.</li>
          </ul>
        </div>
        <div>
          <h3>Annual Scorecard for Participants</h3>
          <ul className="list">
            <li>
              Showcasing individual growth trajectory and readiness for future
              roles.
            </li>
            <li>
              Highlighting performance in skill-building, mentorship
              participation, and business contributions.
            </li>
          </ul>
        </div>
        <div>
          <h3>SMART Goals & Success Metrics</h3>
          <ul className="list">
            <li>
              Skill Readiness Impact – _____% of participants achieving a higher
              RRI score.
            </li>
            <li>
              Leadership Pipeline – Number of STEP participants transitioning
              into expanded roles.
            </li>
            <li>
              Engagement Index – Active participation in learning, mentorship,
              and business initiatives.
            </li>
            <li>
              Mentorship Effectiveness – Feedback scores from participants and
              mentors.
            </li>
            <li>
              Retention & Growth – Increased retention of top talent and career
              progression data.
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default GovernanceMetrics;
