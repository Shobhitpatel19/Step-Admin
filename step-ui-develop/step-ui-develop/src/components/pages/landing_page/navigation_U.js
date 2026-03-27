import React, { useEffect, useState } from "react";
import "../landing_page/navigation.css";
import Footer from "./footer";
import SideProfile from "../../common/sideprofile/SideProfile";
import step_logo from "../../../assets/step_logo_new.svg";
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
export function NavbarForU({ hideContent }) {
  const [profilePicture, setProfilePicture] = useState(null);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [emailAdd, setEmailAdd] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const { uuiModals } = useUuiContext();

  const userDropDownItems = [
    {
      key: "candidateAspiration",
      caption: "Candidate Aspiration",
      path: "/aspiration",
    },
  ];
  useEffect(() => {
    const token = getTokenFromCookies();
    if (token) {
      const { picture } = decodeToken(token);
      const { email } = decodeToken(token);
      setEmailAdd(email);
      setProfilePicture(picture);
    }
  });

  const renderDropdownItems = (menuConfig) => {
    return menuConfig.map((itemConfig) => (
      <DropdownMenuButton
        key={itemConfig.key}
        caption={itemConfig.caption}
        onClick={() => {
          if (itemConfig.path) {
            navigate(itemConfig.path);
          }
        }}
      />
    ));
  };

  const renderAdminDropdown = (props) => (
    <Dropdown
      className="User"
      renderTarget={(props) => (
        <MainMenuButton
          key={props}
          {...props}
          caption="User"
          className="inner_admin"
        />
      )}
      renderBody={(p) => (
        <DropdownMenuBody key={p} cx="admin-dropdown-body">
          {renderDropdownItems(userDropDownItems)}
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
      render: (p) => <MainMenuLogo key={p.id} href="/" logoUrl={step_logo} />,
    },
    {
      id: "WELCOME",
      priority: 9,
      render: (p) => (
        <MainMenuButton
          key={p.id}
          href="/welcome_u"
          caption="Welcome"
          className="welcome_section"
        />
      ),
      caption: "Welcome",
    },
    {
      id: "User",
      priority: 7,
      render: renderAdminDropdown,
      caption: "User",
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
        <div>
          <Banner
            title={`Welcome, ${firstName}`}
            description="STEP is a structured talent development program that identifies and nurtures top internal talent early in their careers. It accelerates growth through career planning, training, mentoring, and personalized action plans, preparing participants for future roles within their competencies. The program equips high-potential employees with the skills necessary to drive the future of their respective domains."
            backlink="/welcome_u"
          />
          <TopTalentSection />
          <FeedbackStages />
          <Footer />
        </div>
      )}
      {isProfileOpen && emailAdd && (
        <SideProfile
          emailAdd={emailAdd}
          isOpen={isProfileOpen}
          onClose={() => setIsProfileOpen(false)}
        />
      )}
    </>
  );
}
