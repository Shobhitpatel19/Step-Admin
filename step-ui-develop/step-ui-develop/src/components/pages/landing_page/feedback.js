import React, { useEffect, useRef } from "react";
import "./feedback.css";
import feedBack1 from "../../../assets/feedBack1.png";
import feedBack2 from "../../../assets/feedBack2.png";
import feedBack3 from "../../../assets/feedBack3.png";

const FeedbackStages = () => {
  const feedbackData = [
    {
      number: "01",
      title: "Content in Progress",
      description: [""],
      imgSrc: feedBack1,
    },
    {
      number: "02",
      title: "Content in Progress",
      description: [""],
      imgSrc: feedBack2,
    },
    {
      number: "03",
      title: "Content in Progress",
      description: [""],
      imgSrc: feedBack3,
    },
  ];

  const containerRef = useRef(null);

  useEffect(() => {
    const feedbackStages = document.querySelectorAll(".feedback-stage");

    function showStages() {
      feedbackStages.forEach((stage, index) => {
        setTimeout(() => {
          stage.classList.add("visible");
        }, index * 500);
      });
    }

    showStages();

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible");
          } else {
            entry.target.classList.remove("visible");
          }
        });
      },
      { threshold: 0.2 }
    );

    const elements = document.querySelectorAll(".feedback-stage");
    elements.forEach((el) => observer.observe(el));

    return () => {
      elements.forEach((el) => observer.unobserve(el));
    };
  }, []);

  return (
    <div className="feedback-container" ref={containerRef}>
      <h1 className="feedback-heading">How STEP Works</h1>
      <div className="feedback-stages">
        {feedbackData.map((stage, index) => (
          <div
            className={`feedback-stage ${
              index % 2 === 0 ? "normal" : "reverse"
            }`}
            key={stage.number}
          >
            <div className="feedback-info">
              <div className="feedback-number">{stage.number}</div>
              <h2 className="feedback-title">{stage.title}</h2>
              <ul className="feedback-description">
                {stage.description.map((desc, idx) => (
                  <li key={`${stage.number}-${idx}`}>{desc}</li>
                ))}
              </ul>
            </div>
            <div>
              <img
                className="feedback-image"
                src={stage.imgSrc}
                alt={stage.title}
              />
            </div>
          </div>
        ))}
      </div>
      <div className="feedback-line-container">
        <svg width="100%" height="100%"></svg>
      </div>
    </div>
  );
};

export default FeedbackStages;
