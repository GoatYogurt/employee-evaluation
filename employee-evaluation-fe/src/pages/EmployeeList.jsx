import EmployeeTable from "../components/EmployeeTable";
import '../index.css';
import { useNavigate } from 'react-router-dom';

function EmployeeList() {
    const navigate = useNavigate();

    return (
        <div>
            <button onClick={() => navigate('/employee-add')}>Add Employee</button>
            <EmployeeTable />
        </div>
    )
}
export default EmployeeList;