import LoginPage from "./pages/LoginPage";
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import './App.css';
import Home from './pages/Home';
import EmployeeView from './pages/EmployeeView';
import Dashboard from './pages/Dashboard';
import EmployeeAdd from './pages/EmployeeAdd';
import EmployeeList from './pages/EmployeeList';
import EvaluationCycleList from './pages/EvaluationCycleList';
import RegisterPage from "./pages/RegisterPage";
import ChangePasswordPage from "./pages/ChangepasswordPage";  
import CriterionList from "./pages/CriterionList";
import CriterionView from "./pages/CriterionView";
import CriterionAdd from "./pages/CriterionAdd";
import CriterionGroupList from "./pages/CriterionGroupList";
import CriterionGroupAdd from "./pages/CriterionGroupAdd";
import CriterionGroupView from "./pages/CriterionGroupView";
import ProjectList from "./pages/ProjectList";
import EmployeeAddOld from "./pages/EmployeeAddOld";


function AppLayout() {
  return (
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
        <Route path="/employee-add-old" element={<Dashboard><EmployeeAddOld /></Dashboard>} />

        <Route path='/criterion-list' element={<Dashboard><CriterionList /></Dashboard>} />
        <Route path='/criterion-view/:id' element={<Dashboard><CriterionView /></Dashboard>} />
        <Route path="/criterion-add" element={<Dashboard><CriterionAdd /></Dashboard>} />

        <Route path='/criterion-group-list' element={<Dashboard><CriterionGroupList /></Dashboard>} />
        <Route path='/criterion-group-view/:id' element={<Dashboard><CriterionGroupView /></Dashboard>} />
        <Route path="/criterion-group-add" element={<Dashboard><CriterionGroupAdd /></Dashboard>} />

        <Route path='/project-list' element={<Dashboard><ProjectList /></Dashboard>}></Route>
        {/* <Route path='/project-add' element={<Dashboard><ProjectAdd /></Dashboard>}></Route> */}

        <Route path="/evaluation-cycle-list" element={<Dashboard><EvaluationCycleList /></Dashboard>} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppLayout />
    </BrowserRouter>
  );
}
