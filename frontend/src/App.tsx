import { Provider, useSelector } from 'react-redux';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { store } from './store/store';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import PropertiesPage from './pages/properties/PropertiesPage';
import TenantsPage from './pages/tenants/TenantsPage';
import ProfileEditPage from './pages/profile/ProfileEditPage';
import type { RootState } from './store/store';
import type { ReactElement } from 'react';

const ProtectedRoute = ({ children }: { children: ReactElement }) => {
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/properties"
          element={
            <ProtectedRoute>
              <PropertiesPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/tenants"
          element={
            <ProtectedRoute>
              <TenantsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile/edit"
          element={
            <ProtectedRoute>
              <ProfileEditPage />
            </ProtectedRoute>
          }
        />
        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function App() {
  try {
    return (
      <Provider store={store}>
        <AppRoutes />
      </Provider>
    );
  } catch (error) {
    console.error('App Error:', error);
    return (
      <div style={{ padding: '20px' }}>
        <h1>Error Loading App</h1>
        <p>{String(error)}</p>
      </div>
    );
  }
}

export default App;
