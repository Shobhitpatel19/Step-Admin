import React from "react";
import PropTypes from "prop-types";
import { FlexRow } from "@epam/uui-components";
import { LinkButton } from "@epam/uui";
import { useNavigate } from "react-router-dom";
import { ReactComponent as navigationBack } from "@epam/assets/icons/common/navigation-back-18.svg";
import "./Banner.css";
import banner_image from "../../../assets/header_banner_background.png";

const Banner = ({
  title,
  description,
  backgroundImage,
  backlinkCaption,
  backlink,
  pageTitle,
  pageDescription,
  isBackLinkVisible = false,
  onBackLinkClick,
}) => {
  const navigate = useNavigate();

  const handleBackLinkClick = () => {
    if (onBackLinkClick) {
      onBackLinkClick();
    } else if (backlink) {
      navigate(backlink);
    }
  };

  return (
    <div className="app-container">
      <div className="banner-scroll uui-scroll-bars uui-shadow-top uui-shadow-bottom">
        <div className="scroll-content">
          <div className="page-container">
            <div
              className="header"
              style={{ backgroundImage: `url(${banner_image})` }}
            >
              <div className="header-container">
                {isBackLinkVisible && (
                  <FlexRow
                    style={{ display: "flex", alignItems: "center" }}
                    onClick={handleBackLinkClick}
                  >
                    <LinkButton
                      caption={backlinkCaption || "Welcome"}
                      color="white"
                      size="36"
                      icon={navigationBack}
                    />
                    <span
                      style={{
                        marginLeft: "8px",
                        color: "white",
                        fontWeight: "bold",
                      }}
                    >
                      {backlinkCaption}
                    </span>
                  </FlexRow>
                )}
                <h1 className="header-title">{title || pageTitle}</h1>
                <div className="header-description">
                  {description || pageDescription}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

Banner.propTypes = {
  title: PropTypes.string,
  description: PropTypes.string,
  backgroundImage: PropTypes.string,
  backlinkCaption: PropTypes.string,
  backlink: PropTypes.string,
  pageTitle: PropTypes.string,
  pageDescription: PropTypes.string,
  isBackLinkVisible: PropTypes.bool,
  onBackLinkClick: PropTypes.func,
};

export default Banner;
