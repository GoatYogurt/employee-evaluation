import LoginPage from "./pages/LoginPage";
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import './App.css';
import Home from './pages/Home';
import EmployeeView from './pages/EmployeeView';
import Dashboard from './pages/Dashboard';
import EmployeeAdd from './pages/EmployeeAdd';
import CriterionList from './pages/CriterionList';
import EmployeeList from './pages/EmployeeList';
import CriterionAdd from './pages/CriterionAdd';
import CriterionView from './pages/CriterionView';
import EvaluationCycleList from './pages/EvaluationCycleList';
import RegisterPage from "./pages/RegisterPage";
import ChangePasswordPage from "./pages/ChangepasswordPage";
// import ProjectList from "./pages/ProjectList";

function AppLayout() {
  const location = useLocation();

  // Các trang KHÔNG cần Dashboard layout
  const noDashboardPaths = ["/", "/register", "/change-password"];
  const shouldUseDashboard = !noDashboardPaths.includes(location.pathname);

  return (
    <>
      <Routes>
        {/* Các route không cần Dashboard */}
        <Route path='/' element={<LoginPage />} />
        <Route path='/register' element={<RegisterPage />} />
        <Route path="/change-password" element={<ChangePasswordPage />} />

        {/* Các route có Dashboard layout */}
        <Route path='/home' element={<Dashboard><Home /></Dashboard>} />
        <Route path='/employee-list' element={<Dashboard><EmployeeList /></Dashboard>} />
        <Route path='/employee-view/:id' element={<Dashboard><EmployeeView /></Dashboard>} />
        <Route path="/employee-add" element={<Dashboard><EmployeeAdd /></Dashboard>} />

        <Route path='/criterion-list' element={<Dashboard><CriterionList /></Dashboard>} />
        <Route path="/criterion-view/:id" element={<Dashboard><CriterionView /></Dashboard>} />
        <Route path="/criterion-add" element={<Dashboard><CriterionAdd /></Dashboard>} />

        {/* <Route path='/project-list' element={<Dashboard><ProjectList /></Dashboard>} /> */}

        <Route path="/evaluation-cycle-list" element={<Dashboard><EvaluationCycleList /></Dashboard>} />
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
