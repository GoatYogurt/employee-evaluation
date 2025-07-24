import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import EmployeeTable from './components/EmployeeTable'

function App() {
  return (
    <div>
      <h1>HR Management</h1>
      <EmployeeTable />
    </div>
  )
}

export default App
