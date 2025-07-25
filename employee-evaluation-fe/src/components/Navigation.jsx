import '../index.css';

function Navigation() {
    return (
        <nav className="navbar">
            <a href="/" className="nav-link">Home</a>
            <a href="/employee-list" className="nav-link">Employee List</a>
            <a href='/criterion-list' className="nav-link">Criterion List</a>
        </nav>
    );
}

export default Navigation;