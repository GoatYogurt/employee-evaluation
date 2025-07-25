import '../index.css';

function Navigation() {
    return (
        <nav className="navbar">
            <a href="/" className="nav-link">Home</a>
            <a href="/employee-list" className="nav-link">Employee List</a>
            <a href='/criterion-list' className="nav-link">Criterion List</a>
            <a href="/evaluation-cycle-list" className="nav-link">Evaluation Cycles</a>
        </nav>
    );
}

export default Navigation;