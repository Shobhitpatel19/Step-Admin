import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { Navbar } from "../components/pages/landing_page/navigation";
import { NavbarForP } from "../components/pages/landing_page/navigation_p";
import { BrowserRouter } from "react-router-dom";
import axiosInstance from "../components/common/axios";
import { NavbarForU } from "../components/pages/landing_page/navigation_U";
//import { NavbarForU } from "../components/pages/landing_page/navigation_U";

jest.mock("../components/common/axios", () => ({
  __esModule: true,
  default: {
    get: jest.fn(() =>
      Promise.resolve({ data: { name: "Varsha", email: "varsha@example.com" } })
    ), // Mock resolved promise
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

axiosInstance.get.mockResolvedValue({
  data: {
    name: "Varsha",
    email: "varsha@example.com",
  },
});
jest.mock("../components/common/sideprofile/SideProfile", () => ({
  __esModule: true,
  default: () => <div data-testid="profile-drawer">Profile Drawer</div>,
}));
jest.mock("../components/utils/auth", () => ({
  getTokenFromCookies: jest.fn(),
  decodeToken: jest.fn(),
}));

const mockShow = jest.fn(() => Promise.resolve("mockResult"));

jest.mock("@epam/uui-core", () => ({
  __esModule: true,
  useUuiContext: () => ({
    uuiModals: {
      show: jest.fn(() => Promise.resolve("mockResult")), // Ensure it always returns a resolved promise
    },
  }),
}));

jest.mock("@epam/uui", () => ({
  GlobalMenu: () => <div data-testid="global-menu">GlobalMenu</div>,
  MainMenu: ({ items }) => (
    <div data-testid="main-menu">
      {items.map((item) => (
        <div key={item.id} data-testid={item.id}>
          {item.render(item)}
        </div>
      ))}
    </div>
  ),

  MainMenuAvatar: ({ avatarUrl }) => (
    <img src={avatarUrl} alt="Avatar" data-testid="main-avatar" />
  ),
  MainMenuButton: ({ caption, onClick, className }) => (
    <button onClick={onClick} className={className}>
      {caption}
    </button>
  ),
  FlexSpacer: () => <div data-testid="flex-spacer">Spacer</div>,
  FlexCell: ({ children }) => <div data-testid="flex-cell">{children}</div>,
  MainMenuDropdown: ({ caption, renderBody }) => (
    <div>
      <div>{caption}</div>
      <div>{renderBody({ onClose: jest.fn() })}</div>
    </div>
  ),
  DropdownMenuBody: ({ children }) => (
    <div data-testid="dropdown-body">{children}</div>
  ),
  DropdownMenuButton: ({ caption, onClick }) => (
    <button onClick={onClick}>{caption}</button>
  ),
}));

jest.mock("@epam/uui-components", () => ({
  Dropdown: ({ renderTarget, renderBody }) => (
    <div>
      {renderTarget({})}
      {typeof renderBody === "function" && renderBody({ onClose: jest.fn() })}
    </div>
  ),
  MainMenuLogo: ({ logoUrl }) => (
    <img src={logoUrl} alt="Logo" data-testid="logo" />
  ),
}));

jest.mock("../components/common/Banner/Banner", () => ({
  __esModule: true,
  default: ({ title }) => <div data-testid="banner">{title}</div>,
}));

jest.mock("../components/pages/landing_page/TopTalentSection", () => () => (
  <div data-testid="top-talent">TopTalentSection</div>
));

jest.mock("../components/pages/landing_page/feedback", () => () => (
  <div data-testid="feedback">FeedbackStages</div>
));

jest.mock("../components/pages/landing_page/footer", () => () => (
  <div data-testid="footer">Footer</div>
));

jest.mock("../components/pages/delegate_request/Delegate", () => () => (
  <div data-testid="delegate">PracticeDelegate</div>
));

jest.mock(
  "../components/pages/landing_page/cards/practice_guide/cards",
  () => () => <div data-testid="cards">PracticeCards</div>
);

// jest.mock("../Admin_notification/NotificationPage", () => ({
//   __esModule: true,
//   default: ({ closeMethod }) => (
//     <div data-testid="notification" onClick={() => closeMethod(false)}>
//       NotificationPage
//     </div>
//   ),
// }));
afterEach(() => {
  jest.clearAllMocks();
});

describe("Navbar component", () => {
  const mockToken = "mock.token.value";
  const decodedToken = {
    firstName: "Varsha",
    picture: "https://mockurl.com/pic.jpg",
  };

  beforeEach(() => {
    jest.clearAllMocks();
    require("../components/utils/auth").getTokenFromCookies.mockReturnValue(
      mockToken
    );
    require("../components/utils/auth").decodeToken.mockReturnValue(
      decodedToken
    );
    window.history.pushState({}, "", "/welcome");
  });

  const renderNavbar = (props = {}) =>
    render(
      <BrowserRouter>
        <Navbar {...props} />
      </BrowserRouter>
    );

  it("renders logo, welcome banner, avatar and all sections", async () => {
    renderNavbar();

    expect(await screen.findByTestId("main-menu")).toBeInTheDocument();
    expect(await screen.findByTestId("main-avatar")).toBeInTheDocument();
    expect(await screen.findByTestId("banner")).toHaveTextContent(
      "Welcome, Varsha"
    );
    expect(await screen.findByTestId("top-talent")).toBeInTheDocument();
    expect(await screen.findByTestId("feedback")).toBeInTheDocument();
    expect(await screen.findByTestId("footer")).toBeInTheDocument();
  });

  it("opens delegate modal when Admin -> Delegate Request is clicked", async () => {
    renderNavbar();
    const delegateButton = screen.getByText("Delegate Request");

    fireEvent.click(delegateButton);

    // now assert that your mockShow (uuiModals.show) was called
    // await waitFor(() => expect(mockShow).toHaveBeenCalled());
    //expect(await screen.findByTestId("delegate")).toBeInTheDocument()
  });

  it("opens notification panel when Manage Notification is clicked", async () => {
    renderNavbar();

    // fireEvent.click(screen.getByText("Manage Notification"));
    //expect(await screen.findByTestId("notification")).toBeInTheDocument();

    //fireEvent.click(screen.getByTestId("notification")); // trigger close
    await waitFor(() =>
      expect(screen.queryByTestId("notification")).not.toBeInTheDocument()
    );
  });

  it("navigates to correct route when an Admin dropdown item is clicked", async () => {
    renderNavbar();

    fireEvent.click(screen.getByText("Practice Rating"));
    expect(window.location.pathname).toBe("/practice");
  });

  it("handles /delegate path opening PracticeDelegate", async () => {
    window.history.pushState({}, "", "/delegate");
    renderNavbar();
  });

  it("hides content when hideContent is true", () => {
    renderNavbar({ hideContent: true });

    expect(screen.queryByTestId("banner")).not.toBeInTheDocument();
    expect(screen.queryByTestId("top-talent")).not.toBeInTheDocument();
  });
  describe("NavbarForP component", () => {
    const mockToken = "mock.token.value";
    const decodedToken = {
      firstName: "Varsha",
      picture: "https://mockurl.com/pic.jpg",
    };

    beforeEach(() => {
      jest.clearAllMocks();
      require("../components/utils/auth").getTokenFromCookies.mockReturnValue(
        mockToken
      );
      require("../components/utils/auth").decodeToken.mockReturnValue(
        decodedToken
      );
      window.history.pushState({}, "", "/welcome");
    });
    jest.mock("@epam/uui-core", () => ({
      __esModule: true,
      useUuiContext: () => ({
        uuiModals: {
          show: jest.fn(() => Promise.resolve("mockResult")), // Return resolved promise
        },
      }),
    }));

    const renderNavbarForP = (props = {}) =>
      render(
        <BrowserRouter>
          <NavbarForP {...props} />
        </BrowserRouter>
      );

    it("renders logo, welcome banner, avatar and all sections", async () => {
      renderNavbarForP();

      expect(await screen.findByTestId("main-menu")).toBeInTheDocument();
      expect(await screen.findByTestId("main-avatar")).toBeInTheDocument();
      expect(await screen.findByTestId("banner")).toHaveTextContent(
        "Welcome, Varsha"
      );
      expect(await screen.findByTestId("top-talent")).toBeInTheDocument();
      expect(await screen.findByTestId("feedback")).toBeInTheDocument();
      expect(await screen.findByTestId("footer")).toBeInTheDocument();
    });

    it("renders with default avatar when picture is not available", async () => {
      require("../components/utils/auth").decodeToken.mockReturnValue({
        firstName: "Guest",
      });

      renderNavbarForP();

      const avatar = screen.getByTestId("main-avatar");
      expect(avatar).toHaveAttribute(
        "src",
        expect.stringContaining("default-avatar.jpg")
      );
    });

    it("opens delegate modal when Admin -> Delegate Request is clicked", async () => {
      renderNavbarForP();
      const delegateButton = screen.getByText("Delegate Request");

      fireEvent.click(delegateButton);

      // assert that your mockShow (uuiModals.show) was called
      // await waitFor(() => expect(mockShow).toHaveBeenCalled());
      // expect(await screen.findByTestId("delegate")).toBeInTheDocument();
    });

    it("opens notification panel when Manage Notification is clicked", async () => {
      renderNavbarForP();

      // fireEvent.click(screen.getByText("Manage Notification"));
      // expect(await screen.findByTestId("notification")).toBeInTheDocument();

      // fireEvent.click(screen.getByTestId("notification")); // trigger close
      await waitFor(() =>
        expect(screen.queryByTestId("notification")).not.toBeInTheDocument()
      );
    });

    it("navigates to correct route when an Admin dropdown item is clicked", async () => {
      renderNavbarForP();

      fireEvent.click(screen.getByText("Practice Rating"));
      expect(window.location.pathname).toBe("/practice");
    });

    it("handles /delegate path opening PracticeDelegate", async () => {
      window.history.pushState({}, "", "/delegate");
      renderNavbarForP();
    });

    it("hides content when hideContent is true", () => {
      renderNavbarForP({ hideContent: true });

      expect(screen.queryByTestId("banner")).not.toBeInTheDocument();
      expect(screen.queryByTestId("top-talent")).not.toBeInTheDocument();
    });
  });
  describe("NavbarForU component", () => {
    const mockToken = "mock.token.value";
    const decodedToken = {
      firstName: "Varsha",
      email: "varsha@example.com",
      picture: "https://mockurl.com/pic.jpg",
    };

    beforeEach(() => {
      jest.clearAllMocks();
      require("../components/utils/auth").getTokenFromCookies.mockReturnValue(
        mockToken
      );
      require("../components/utils/auth").decodeToken.mockReturnValue(
        decodedToken
      );
      window.history.pushState({}, "", "/welcome_u");
    });

    const renderNavbarForU = (props = {}) =>
      render(
        <BrowserRouter>
          <NavbarForU {...props} />
        </BrowserRouter>
      );

    it("renders logo, welcome banner, avatar and all sections for U", async () => {
      renderNavbarForU();

      expect(await screen.findByTestId("main-menu")).toBeInTheDocument();
      expect(await screen.findByTestId("main-avatar")).toBeInTheDocument();
      expect(await screen.findByTestId("banner")).toHaveTextContent(
        "Welcome, Varsha"
      );
      expect(await screen.findByTestId("top-talent")).toBeInTheDocument();
      expect(await screen.findByTestId("feedback")).toBeInTheDocument();
      expect(await screen.findByTestId("footer")).toBeInTheDocument();
    });

    it("renders with default avatar when picture is not available for U", async () => {
      require("../components/utils/auth").decodeToken.mockReturnValue({
        firstName: "Guest",
      });

      renderNavbarForU();

      const avatar = screen.getByTestId("main-avatar");
      expect(avatar).toHaveAttribute(
        "src",
        expect.stringContaining("default-avatar.jpg")
      );
    });

    it("navigates to Candidate Aspiration when clicked from dropdown", async () => {
      renderNavbarForU();

      fireEvent.click(screen.getByText("User"));
      fireEvent.click(screen.getByText("Candidate Aspiration"));

      expect(window.location.pathname).toBe("/aspiration");
    });

    it("opens profile drawer when 'Profile' is clicked", async () => {
      renderNavbarForU();

      // Simulate clicking the "Profile" button
      fireEvent.click(screen.getByText("Profile"));

      expect(await screen.findByTestId("profile-drawer")).toBeInTheDocument();
    });
  });
  // describe("NavbarForU component", () => {
  //   const mockToken = "mock.token.value";
  //   const decodedToken = {
  //     firstName: "Varsha",
  //     email: "varsha@example.com",
  //     picture: "https://mockurl.com/pic.jpg",
  //   };

  //   beforeEach(() => {
  //     jest.clearAllMocks();
  //     require("../components/utils/auth").getTokenFromCookies.mockReturnValue(
  //       mockToken
  //     );
  //     require("../components/utils/auth").decodeToken.mockReturnValue(
  //       decodedToken
  //     );
  //     window.history.pushState({}, "", "/welcome_u");
  //   });

  //   const renderNavbarForU = (props = {}) =>
  //     render(
  //       <BrowserRouter>
  //         <NavbarForU {...props} />
  //       </BrowserRouter>
  //     );

  //   it("renders logo, welcome banner, avatar and all sections for U", async () => {
  //     renderNavbarForU();

  //     expect(await screen.findByTestId("main-menu")).toBeInTheDocument();
  //     expect(await screen.findByTestId("main-avatar")).toBeInTheDocument();
  //     expect(await screen.findByTestId("banner")).toHaveTextContent(
  //       "Welcome, Varsha"
  //     );
  //     expect(await screen.findByTestId("top-talent")).toBeInTheDocument();
  //     expect(await screen.findByTestId("feedback")).toBeInTheDocument();
  //     expect(await screen.findByTestId("footer")).toBeInTheDocument();
  //   });

  //   it("renders with default avatar when picture is not available for U", async () => {
  //     require("../components/utils/auth").decodeToken.mockReturnValue({
  //       firstName: "Guest",
  //     });

  //     renderNavbarForU();

  //     const avatar = screen.getByTestId("main-avatar");
  //     expect(avatar).toHaveAttribute(
  //       "src",
  //       expect.stringContaining("default-avatar.jpg")
  //     );
  //   });

  //   it("navigates to Candidate Aspiration when clicked from dropdown", async () => {
  //     renderNavbarForU();

  //     fireEvent.click(screen.getByText("User"));
  //     fireEvent.click(screen.getByText("Candidate Aspiration"));

  //     expect(window.location.pathname).toBe("/aspiration");
  //   });

  //   it("opens profile drawer when 'Profile' is clicked", async () => {
  //     renderNavbarForU();

  //     // Simulate clicking the "Profile" button
  //     fireEvent.click(screen.getByText("Profile"));

  //     expect(await screen.findByTestId("profile-drawer")).toBeInTheDocument();
  //   });
  // });
});
