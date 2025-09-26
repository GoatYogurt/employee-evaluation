import LoginPage from "./pages/LoginPage";
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import './App.css';
import Home from './pages/Home';
import EmployeeView from './pages/EmployeeView';
import Navigation from './components/Navigation';
import EmployeeAdd from './pages/EmployeeAdd';
import CriterionList from './pages/CriterionList';
import EmployeeList from './pages/EmployeeList';
import CriterionAdd from './pages/CriterionAdd';
import CriterionView from './pages/CriterionView';
import EvaluationCycleList from './pages/EvaluationCycleList';
import RegisterPage from "./pages/RegisterPage";
import ChangePasswordPage from "./pages/ChangepasswordPage";

function AppLayout() {
  const location = useLocation();

  // Các trang KHÔNG cần Navigation
  const hideNavPaths = ["/", "/register", "/change-password"];
  const shouldHideNav = hideNavPaths.includes(location.pathname);

  return (
    <>
      {!shouldHideNav && <Navigation />}
      <Routes>
        {/* Các route không cần Navigation */}
        <Route path='/' element={<LoginPage />} />
        <Route path='/register' element={<RegisterPage />} />
        <Route path="/change-password" element={<ChangePasswordPage />} />

        {/* Các route có Navigation */}
        <Route path='/home' element={<Home />} />
        <Route path='/employee-list' element={<EmployeeList />} />
        <Route path='/employee-view/:id' element={<EmployeeView />} />
        <Route path="/employee-add" element={<EmployeeAdd />} />

        <Route path='/criterion-list' element={<CriterionList />} />
        <Route path="/criterion-view/:id" element={<CriterionView />} />
        <Route path="/criterion-add" element={<CriterionAdd />} />

        <Route path="/evaluation-cycle-list" element={<EvaluationCycleList />} />
      </Routes>
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppLayout />
    </BrowserRouter>
  );
}
