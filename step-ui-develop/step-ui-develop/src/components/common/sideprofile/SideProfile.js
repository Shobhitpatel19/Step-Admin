import React, { useState, useEffect } from "react";
import TopDivision from "./TopDivision";
import BottomDivision from "./BottomDivision";
import "../../styling/side-profile.css";
import axiosInstance from "../axios";
const SideProfile = ({ emailAdd, isOpen, onClose }) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const apiUrl = `step/employee-profile?email=${encodeURIComponent(emailAdd)}`;

  useEffect(() => {
    if (!emailAdd || !isOpen) return;
    axiosInstance
      .get(apiUrl)
      .then((response) => {
        console.log(response);
        setData(response.data);
        console.log(response.data);
        setLoading(false);
      })
      .catch((error) => {
        setError(error.message);
        setLoading(false);
      });
  }, [emailAdd, isOpen]);
  if (loading) return <p></p>;
  if (!isOpen) return null;

  if (error) return <p>Error: {error}</p>;

  const keysForFirstJson = [
    "fullName",
    "jobDesignation",
    "officeAddress",
    "photo",
    "employmentId",
  ];

  const { firstJson, secondJson } = Object.entries(data).reduce(
    (acc, [key, value]) => {
      if (keysForFirstJson.includes(key)) {
        acc.firstJson[key] = value;
      } else {
        acc.secondJson[key] = value;
      }
      return acc;
    },
    { firstJson: {}, secondJson: {} }
  );

  const {
    firstName,
    lastName,
    profileType,
    jobTrack,
    jobTrackLevel,
    unit,
    email,
    ...filteredSecondJson
  } = secondJson;
  const jobLevel = `${jobTrack} Level ${jobTrackLevel}`;
  const combinedSecondJson = {
    ...filteredSecondJson,
    jobLevel: jobLevel,
  };

  return (
    <div className="side-profile-backdrop">
      <div className="side-profile-main">
        <button className="close-btn" onClick={onClose}>
          x
        </button>
        {loading && <p>Loading...</p>}
        {error && <p>Error: {error}</p>}
        {!loading && !error && data && (
          <>
            <TopDivision data={firstJson} />
            <BottomDivision data={combinedSecondJson} />
          </>
        )}
      </div>
    </div>
  );
};

export default SideProfile;
