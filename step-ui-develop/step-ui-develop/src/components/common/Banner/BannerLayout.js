import React from "react";
import { Outlet, useLocation } from "react-router-dom";
import Banner from "./Banner";

const bannerConfig = {
  "/culture-score": {
    title: "Admin - Culture Score",
    description:
      "Gain valuable insights, identify areas for improvement, and foster personal and professional growth.",
    isBackLinkVisible: true,
    backlink: "/welcome",
  },
  "/aspiration": {
    title: "Candidate Aspiration",
    pageDescription: "Explore the Candidate Aspirations and set goals",
    isBackLinkVisible: true,
    backlink: "/welcome_u",
  },

  "/merit-list": {
    title: "Admin - Initial Identification Merit List",
    description:
      "Gain valuable insights, identify areas for improvement, and foster personal and professional growth.",
    isBackLinkVisible: true,
    caption: "Welcome",
    backlink: "/welcome",
  },
  "/engx-extra-mile": {
    title: "Admin - Contributions (EngX and Extra Mile Activities)",
    description:
      "Gain valuable insights, identify areas for improvement, and foster personal and professional growth.",
    isBackLinkVisible: true,
    caption: "Welcome",
    backlink: "/welcome",
  },
  "/view-master-data": {
    title: "Identify Final Merit List",
    description:
      "This page provides a comprehensive overview of the Master Excel, displaying all key records and details in a structured, tabular format. Users can view, filter, sort, and search data entries with ease.",
    isBackLinkVisible: true,
    backlink: "/welcome",
  },
  "/identification": {
    title: "Identification Phase",
    description:
      "The Identification Phase is the foundation of the selection process, ensuring that only high-performing employees are considered for the program. This step involves evaluating employees based on predefined performance criteria to create a qualified talent pool.",
  },
  "/governance-metrics": {
    title: "Governance & Success Metrics for STEP",
    description:
      "To ensure the STEP (Strive to Excellence Program) operates with strong governance and measurable impact, we are establishing a structured STEP Core Team that will oversee both Identification and Engagement phases.",
  },
  "/engagement": {
    title: "STEP - Engagement Phase Overview",
    description:
      "Once participants are selected into the STEP (Strive to Excellence Program) through the identification process, the next crucial phase is Engagement. This phase is designed to actively develop participants' capabilities and prepare them for future roles within their competency. The engagement journey is structured around a goal-driven, mentor-supported, and future-focused approach, ensuring that participants align their aspirations with business needs and gain the skills necessary to step into leadership or specialized roles.",
  },
  "/div": {
    title: "Welcome , {firstName}",
    description:
      "Gain valuable insights, identify areas for improvement, and foster personal and professional growth.",
    isBackLinkVisible: true,
    backlink: "/welcome",
  },
  "/future_skills": {
    title: "Future Skills",
    description:
      "Empower your practice with future-ready skills to align with strategic goals and leverage the latest technological advancements for transformative growth.",
    isBackLinkVisible: true,
    backlink: "/welcome_p",
  },
 
};

const LayoutForBanner = () => {
  const location = useLocation();
  const currentBannerProps = bannerConfig[location.pathname] || null;

  return (
    <div>
      {currentBannerProps && Object.keys(currentBannerProps).length > 0 && (
        <Banner {...currentBannerProps} />
      )}
      <Outlet />
    </div>
  );
};

export default LayoutForBanner;
