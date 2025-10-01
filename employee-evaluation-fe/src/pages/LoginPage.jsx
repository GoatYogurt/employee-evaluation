import React from 'react';
import '../index.css';
import LoginForm from '../components/LoginForm';

const LoginPage = () => {
  return (
  <section>
    <div className="login-page">
      <div className="main-text">
        <h1>VIETTEL</h1>
        <h3>Employee Evaluation System</h3>
      </div>

      <div className="login-form">
          <LoginForm />
      </div>
    </div>
  </section>
  );
};

export default LoginPage;