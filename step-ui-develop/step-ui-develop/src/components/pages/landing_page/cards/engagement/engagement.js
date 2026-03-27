import React, { useEffect } from "react";
import "../identification/IdentificationPhase.css";
import { NavbarForP } from "../../navigation_p";
import { Navbar } from "../../navigation";
import { setRole } from "../../../../../redux/actions";
import { decodeToken, getTokenFromCookies } from "../../../../utils/auth";
import { useDispatch, useSelector } from "react-redux";
const Engagement = () => {
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
          <h2 className="subtitle" style={{ marginTop: "-20px" }}>
            Key Aspects of Engagement
          </h2>
          <div className="grid">
            <div className="card">
              <p>
                <h3>Future Skills Mapping </h3>
                <br></br>
                Practice Heads outline the critical future skills required for
                their competency, keeping a 2-3 year horizon in mind.
              </p>
            </div>
            <div className="card">
              <p>
                <h3>Aspirations Alignment</h3>
                <br></br>
                Participants align their career goals with these identified
                future skills to create a clear growth roadmap.
              </p>
            </div>
            <div className="card">
              <p>
                <h3>Readiness Evaluation </h3>
                <br></br>
                Practice Heads assess each participant’s current positioning
                against the future skills requirement.
              </p>
            </div>
            <div className="card">
              <p>
                <h3>Development Planning </h3>
                <br></br>
                Employees build individual development plans (IDPs) to bridge
                skill gaps and enhance role readiness.
              </p>
            </div>
            <div className="card">
              <p>
                <h3>Cohort-Based Learning</h3>
                <br></br>
                Participants engage in structured coaching, mentoring, and
                cohort-based learning to accelerate their development.
              </p>
            </div>
            <div className="card">
              <p>
                <h3>Tracking & Accountability </h3>
                <br></br>A yearly development scorecard is shared with
                participants, reflecting their progress on the Role Readiness
                Index, providing a clear view of their journey and areas for
                improvement.
              </p>
            </div>
          </div>

          <p className="description">
            This structured engagement model ensures that STEP participants stay
            on track, receive the right mentorship, and actively work towards
            their professional growth, ultimately contributing to the
            competency’s long-term success.
          </p>
        </div>
      </div>
    </div>
    // </div>
  );
};

export default Engagement;
