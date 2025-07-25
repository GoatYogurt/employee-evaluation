import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import EmployeeTable from './components/EmployeeTable'
import Home from './pages/Home'
import EmployeeView from './pages/EmployeeView'
import Navigation from './components/Navigation'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import EmployeeAdd from './pages/EmployeeAdd'
import CriterionList from './pages/CriterionList'
import EmployeeList from './pages/EmployeeList'
import CriterionAdd from './pages/CriterionAdd'
import CriterionView from './pages/CriterionView'


function App() {
  return (
    <BrowserRouter>
      <Navigation />
      <Routes>
        <Route path='/' element={<Home />} />

        <Route path='/employee-list' element={<EmployeeList />} />
        <Route path='/employee-view/:id' element={<EmployeeView />} />
        <Route path="/employee-add" element={<EmployeeAdd />} />

        <Route path='/criterion-list' element={<CriterionList />} />
        <Route path="/criterion-view/:id" element={<CriterionView />} />
        <Route path="/criterion-add" element={<CriterionAdd />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
