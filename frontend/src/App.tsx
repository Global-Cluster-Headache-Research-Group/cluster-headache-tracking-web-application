import { Container } from 'react-bootstrap';
import AppNavbar from './components/AppNavbar';
import ReportsTable from './components/ReportsTable';
import { ReportsContextProvider } from './context/ReportsContext';

function App() {
  return (
    <div>
      <AppNavbar />
      <Container>
        <ReportsContextProvider>
          <ReportsTable />
        </ReportsContextProvider>
      </Container>
    </div>
  );
}

export default App;
