import React from "react";
import "../src/components/styling/side-profile.css";
import Aspiration from "./components/pages/candidate_aspiration/AspirationPage";
import { Route, Routes } from "react-router-dom";
import { NavbarForU } from "./components/pages/landing_page/navigation_U";
import LoginButton from "./components/pages/login_page/login";
import { NavbarForP } from "./components/pages/landing_page/navigation_p";
import { Navbar } from "./components/pages/landing_page/navigation";
import ProtectedRoute from "./components/pages/landing_page/proctectedRoute";
import Master from "./components/pages/master_excel_upload/Master";
import CultureScore from "./components/pages/culture_upload/CultureScore";
import PracticeHeadPage from "./components/pages/practice_rating/PracticeHeadPage";
import LayoutForBanner from "./components/common/Banner/BannerLayout";
import Unauthorized from "./components/pages/Unauthorised";
import EngXExtraMile from "./components/pages/engx_extramile_upload/EngXExtraMile";
import MasterExcelView from "./components/pages/master_excel_view/MasterExcelView";
import "../src/components/styling/globalMedia.css";
import IdentificationPhase from "./components/pages/landing_page/cards/identification/identification";
import GovernanceMetrics from "./components/pages/landing_page/cards/hrImpact/goveranace";
import Engagement from "./components/pages/landing_page/cards/engagement/engagement";
import SideProfile from "./components/common/sideprofile/SideProfile";
import { getTokenFromCookies,decodeToken } from "./components/utils/auth";
// import SideProfile from "./components/common/sideprofile/SideProfile";
import NotificationPage from "./components/pages/Admin_notification/NotificationPage";
import FutureSkills from "./components/pages/Future_skills/FutureSkills";

const App = () => {
  const token = getTokenFromCookies(); // or sessionStorage.getItem("token")
  const { email } = decodeToken(token);
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginButton />} />
        <Route path="/session-expired" element={<LoginButton />} />
        <Route path="/login" element={<LoginButton />} />
        <Route path="/logout" element={<LoginButton />} />
        <Route path="/unauthorised" element={<Unauthorized />} />

        <Route element={<LayoutForBanner />}>
          <Route path="/identification" element={<IdentificationPhase />} />
          <Route path="/governance-metrics" element={<GovernanceMetrics />} />
          <Route path="/engagement" element={<Engagement />} />
          <Route
            element={<ProtectedRoute allowedRoles={["ROLE_SA", "ROLE_P"]} />}
          >
            <Route path="/practice" element={<PracticeHeadPage />} />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={["ROLE_SA"]} />}>
            <Route path="/welcome" element={<Navbar />} />
            <Route path="/merit-list" element={<Master />} />
            <Route path="/culture-score" element={<CultureScore />} />
            <Route path="/view-master-data" element={<MasterExcelView />} />
            <Route path="/practice" element={<PracticeHeadPage />} />
            <Route path="/delegate" element={<Navbar />} />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={["ROLE_SA"]} />}>
            <Route path="/welcome" element={<Navbar />} />
            <Route path="/merit-list" element={<Master />} />
            <Route path="/engx-extra-mile" element={<EngXExtraMile />} />
            <Route path="/notification" element={<Navbar/>} />            
          </Route>
          <Route element={<ProtectedRoute allowedRoles={["ROLE_P"]} />}>
            <Route path="/welcome_p" element={<NavbarForP />} />
            <Route path="/practice" element={<PracticeHeadPage />} />
            <Route path="/practice_delegate" element={<NavbarForP />} />
          </Route>
          <Route element={<ProtectedRoute allowedRoles={["ROLE_U"]} />}>
            <Route path="/welcome_u" element={<NavbarForU />} />
            <Route path="/aspiration" element={<Aspiration />} />
            <Route
              path="/side-profile"
              element={<SideProfile emailAdd={email} />}
            />
            <Route path="/future_skills" element={<FutureSkills />} />
           

          </Route>
          
        </Route>
        
        
      </Routes>
    </div>
  );
};
export default App;
