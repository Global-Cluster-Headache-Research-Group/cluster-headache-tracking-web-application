import { Container } from 'react-bootstrap';
import AppNavbar from './components/AppNavbar';
import AttacksTable from './components/Attacks';
import { AttacksContextProvider } from './context/AttacksContext';

function App() {
  return (
    <div>
      <AppNavbar />
      <Container>
        <AttacksContextProvider>
          <AttacksTable />
        </AttacksContextProvider>
      </Container>
    </div>
  );
}

export default App;
