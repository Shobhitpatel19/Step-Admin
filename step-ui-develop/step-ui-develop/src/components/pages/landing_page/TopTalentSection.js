import React from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { Tooltip } from "@epam/uui";
import "./TopTalentSection.css";

const StagesOfTopTalent = () => {
  const cardData = [
    {
      title: "IDENTIFICATION",
      description: "Employee who permanently works for EPAM",
      link: "/identification",
    },
    {
      title: "ENGAGEMENTS",
      description: "Colleagues who work closely with an employee",
      link: "/engagement",
    },
    {
      title: "REPORTS",
      description: "Managers support employee growth with timely feedback",
      tooltip: "Content is under progress",
    },
    {
      title: "GOVERNANCE & SUCCESS METRICS",
      description: "People partner supports employees with feedback processes",
      link: "/governance-metrics",
    },
  ];

  const CustomNextArrow = (props) => {
    const { onClick } = props;
    return <div className="custom-arrow next-arrow" onClick={onClick}></div>;
  };

  const CustomPrevArrow = (props) => {
    const { onClick } = props;
    return <div className="custom-arrow prev-arrow" onClick={onClick}></div>;
  };

  const settings = {
    dots: false,
    infinite: false,
    speed: 500,
    slidesToShow: 4,
    slidesToScroll: 1,
    nextArrow: <CustomNextArrow />,
    prevArrow: <CustomPrevArrow />,
    responsive: [
      {
        breakpoint: 768,
        settings: { slidesToShow: 1, slidesToScroll: 1 },
      },
      {
        breakpoint: 1024,
        settings: { slidesToShow: 2, slidesToScroll: 1 },
      },
    ],
  };

  return (
    <div className="top-talent-container">
      <h2 className="top-talent-heading">Stages of Top Talent</h2>
      <Slider {...settings} className="top-talent-slider">
        {cardData.map((card, index) => (
          <div className="top-talent-card-wrapper" key={index}>
            <div className={`top-talent-card bg-color-${(index % 4) + 1}`}>
              {card.tooltip ? (
                <Tooltip content={card.tooltip} cx="right-start">
                  <a
                    href={card.link}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="top-talent-link"
                  >
                    <h3 className="top-talent-title">{card.title}</h3>
                  </a>
                </Tooltip>
              ) : (
                <a
                  href={card.link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="top-talent-link"
                >
                  <h3 className="top-talent-title">{card.title}</h3>
                </a>
              )}
              <p className="top-talent-description">{card.description}</p>
            </div>
          </div>
        ))}
      </Slider>
    </div>
  );
};

export default StagesOfTopTalent;
