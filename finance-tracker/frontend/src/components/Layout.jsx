import Navbar from './Navbar';
import './Layout.css';

export default function Layout({ children }) {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-content">{children}</main>
    </div>
  );
}
