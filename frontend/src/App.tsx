import { Container } from 'react-bootstrap';
import { QueryClientProvider } from 'react-query';
import AppNavbar from './components/AppNavbar';
import { queryClient } from './services/queryClient';
import Reports from './views/Reports';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppNavbar />
      <Container>
        <Reports />
      </Container>
    </QueryClientProvider>
  );
}

export default App;
