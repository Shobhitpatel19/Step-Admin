import React from "react";
import "./cards.css";
import { Navbar } from "../../navigation";

const PracticeInfo = () => {
  const tasks = [
    {
      title: "Employee Performance Ratings",
      description:
        "Evaluate and rate employees based on their potential for professional growth within the practice.",
      steps: ["Go to MyOrg -> Practice Rating"],
    },
    // {To-do}
    // {
    //   title: "Future Skills Upload",
    //   description:
    //     "Identify and list key skills that will be essential for future success. These skills will help employees in your practice align their development plans.",
    //   steps: ["Go to MyOrg -> Upload Future Skills"],
    // },
    {
      title: "Delegation of Responsibilities",
      description:
        "If you're unable to manage tasks yourself, you have the option to delegate responsibilities to another team member.",
      steps: ["Go to MyOrg -> Delegate Request"],
    },
  ];

  return (
    <div className="practice-info-container">
      <div className="practice-info-content">
        <h2 className="practice-title">Practice head Actions</h2>
        <p className="practice-description">
          To ensure your practice meets the guidelines of step program , you’ll
          need to perform the following tasks:
        </p>

        <div className="practice-flex-container">
          {tasks.map((task, index) => (
            <div key={index} className="practice-card">
              <div className="practice-card-content">
                <h3>{task.title}</h3>
                <p>{task.description}</p>
              </div>
              <div className="practice-card-steps">
                <ul>
                  {task.steps.map((step, idx) => (
                    <li key={idx}>{step}</li>
                  ))}
                </ul>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PracticeInfo;
