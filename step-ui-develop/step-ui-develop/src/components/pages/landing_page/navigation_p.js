import React, { useEffect, useRef, useState } from "react";
import "../landing_page/navigation.css";
import step_logo_new from "../../../assets/step_logo_new.svg";
import Footer from "./footer";
import {
  GlobalMenu,
  MainMenu,
  MainMenuAvatar,
  MainMenuButton,
  FlexSpacer,
  FlexCell,
  MainMenuDropdown,
  DropdownMenuBody,
  DropdownMenuButton,
} from "@epam/uui";
import { Dropdown, MainMenuLogo } from "@epam/uui-components";
import { useLocation, useNavigate } from "react-router-dom";
import { getTokenFromCookies, decodeToken } from "../../utils/auth";
import Banner from "../../common/Banner/Banner";
import TopTalentSection from "./TopTalentSection";
import FeedbackStages from "./feedback";
import { useUuiContext } from "@epam/uui-core";
import PracticeDelegate from "../delegate_request/Delegate";
import axiosInstance from "../../common/axios";
import PracticeInfo from "../../pages/landing_page/cards/practice_guide/cards";
export function NavbarForP({ hideContent }) {
  const [isDelegate, setIsDelegate] = useState(false);
  const [practiceDropDownItems, setPracticeDropDownItems] = useState([]);
  const [profilePicture, setProfilePicture] = useState(null);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [emailAdd, setEmailAdd] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  const { uuiModals } = useUuiContext();

  const isPracticeDelegateShown = useRef(false);

  const isPracticeRatingPage = window.location.pathname === "/practice";

  const openPracticeDelegate = (backtrackPath) => {
    uuiModals
      .show((props) => <PracticeDelegate {...props} />)
      .then((result) => {})
      .catch(() => {
        window.history.replaceState(null, "", backtrackPath);
      });
  };

  useEffect(() => {
    const token = getTokenFromCookies();
    if (token) {
      const { picture, isDelegate } = decodeToken(token);
      const { email } = decodeToken(token);
      setEmailAdd(email);
      setIsDelegate(isDelegate);
      setProfilePicture(picture);
      if (isDelegate) {
        axiosInstance
          .get("step/get-delegated-features")
          .then((response) => {
            if (response) {
              if (response.status) {
                if (response.status === 200) {
                  setPracticeDropDownItems(
                    response.data.map((feature) => {
                      return {
                        key: feature.name,
                        caption: feature.name,
                        path: feature.frontendPath,
                      };
                    })
                  );
                }
              }
            }
          })
          .catch((error) => {
            console.log("Error fetching features");
          });
      } else {
        setPracticeDropDownItems([
          {
            key: "practiceRating",
            caption: "Practice Rating",
            path: "/practice",
          },
          { key: "delegateRequest", caption: "Delegate Request" },
          {
            key: "future-skills",
            caption: "Future Skills",
            path: "/future_skills",
          },
        ]);
      }
    }

    if (
      location.pathname === "/practice_delegate" &&
      !isPracticeDelegateShown.current
    ) {
      isPracticeDelegateShown.current = true;
      setTimeout(() => {
        openPracticeDelegate("/welcome_p");
      }, 100);
    }
  }, []);

  const renderDropdownItems = (menuConfig) => {
    return menuConfig.map((itemConfig) => (
      <DropdownMenuButton
        key={itemConfig.key}
        caption={itemConfig.caption}
        onClick={() => {
          if (itemConfig.key === "delegateRequest") {
            openPracticeDelegate(location.pathname + location.search);
            window.history.pushState(null, "", "/practice_delegate");
          }

          if (itemConfig.path) {
            navigate(itemConfig.path);
          }
        }}
      />
    ));
  };

  const renderMyOrgDropdown = (props) => (
    <Dropdown
      className="Admin"
      renderTarget={(props) => (
        <MainMenuButton {...props} caption="My Org" className="inner_admin" />
      )}
      renderBody={() => (
        <DropdownMenuBody cx="admin-dropdown-body">
          {renderDropdownItems(practiceDropDownItems)}
        </DropdownMenuBody>
      )}
    />
  );

  const renderAvatar = () => {
    return (
      <Dropdown
        key="avatar"
        renderTarget={(props) => (
          <MainMenuAvatar
            avatarUrl={
              profilePicture || "https://example.com/default-avatar.jpg"
            }
            rawProps={{ "aria-label": "User avatar" }}
            isDropdown
            {...props}
          />
        )}
        renderBody={(props) => (
          <DropdownMenuBody {...props}>
            <DropdownMenuButton
              caption="Profile"
              onClick={() => {
                const token = getTokenFromCookies();
                if (token) {
                  const { email } = decodeToken(token);
                  setEmailAdd(email);
                  setIsProfileOpen(true);
                }
              }}
            />
          </DropdownMenuBody>
        )}
        placement="bottom"
      />
    );
  };

  const getMenuItems = () => [
    {
      id: "logo",
      priority: 99,
      render: (p) => (
        <MainMenuLogo key={p.id} href="/" logoUrl={step_logo_new} />
      ),
    },
    {
      id: "WELCOME",
      priority: 9,
      render: (p) => (
        <MainMenuButton
          key={p.id}
          href="/welcome_p"
          caption="Welcome"
          className="welcome_section"
        />
      ),
      caption: "WELCOME",
    },
    {
      id: "MyOrg",
      priority: 7,
      render: renderMyOrgDropdown,
      caption: "MyOrg",
    },
    {
      id: "moreContainer",
      priority: 8,
      collapsedContainer: true,
      render: (item, hiddenItems) => (
        <MainMenuDropdown
          caption="More"
          key={item.id}
          renderBody={(props) => {
            return hiddenItems?.map((i) =>
              i.render({ ...i, onClose: props.onClose })
            );
          }}
        />
      ),
    },
    {
      id: "flexSpacer",
      priority: 100,
      render: (p) => <FlexSpacer key={p.id} />,
    },
    { id: "avatar", priority: 9, render: renderAvatar },
    {
      id: "globalMenu",
      priority: 100,
      render: (p) => <GlobalMenu key={p.id} />,
    },
  ];
  const [firstName, setFirstName] = useState("Guest");

  useEffect(() => {
    const token = getTokenFromCookies();
    if (token) {
      const decodedToken = decodeToken(token);
      setFirstName(decodedToken?.firstName || "Guest");
    }
  }, []);

  return (
    <>
      <div
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "100%",
          zIndex: 1000,
          backgroundColor: "#fff",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        }}
      >
        <FlexCell grow={1}>
          <MainMenu items={getMenuItems()} />
        </FlexCell>
      </div>

      {hideContent || (
        <>
          <Banner
            title={`Welcome, ${firstName}`}
            description="STEP is a structured talent development program that identifies and nurtures top internal talent early in their careers. It accelerates growth through career planning, training, mentoring, and personalized action plans, preparing participants for future roles within their competencies. The program equips high-potential employees with the skills necessary to drive the future of their respective domains."
            backlink="/welcome_p"
          />
          <PracticeInfo />
          <TopTalentSection />
          <FeedbackStages />
          <Footer />
        </>
      )}
    </>
  );
}
