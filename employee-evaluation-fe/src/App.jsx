import './App.css'
import Home from './pages/Home'
import EmployeeView from './pages/EmployeeView'
import Navigation from './components/Navigation'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import EmployeeAdd from './pages/EmployeeAdd'
import CriterionList from './pages/CriterionList'
import EmployeeList from './pages/EmployeeList'
import CriterionAdd from './pages/CriterionAdd'
import CriterionView from './pages/CriterionView'
import EvaluationCycleList from './pages/EvaluationCycleList'
import Login from './pages/Login'

function App() {
  return (
    <BrowserRouter>
      <Navigation />
      <Routes>
        <Route path='/' element={<Home />} />

        <Route path='/login' element={<Login />} />
        <Route path='/employee-list' element={<EmployeeList />} />
        <Route path='/employee-view/:id' element={<EmployeeView />} />
        <Route path="/employee-add" element={<EmployeeAdd />} />

        <Route path='/criterion-list' element={<CriterionList />} />
        <Route path="/criterion-view/:id" element={<CriterionView />} />
        <Route path="/criterion-add" element={<CriterionAdd />} />

        <Route path="/evaluation-cycle-list" element={<EvaluationCycleList />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
