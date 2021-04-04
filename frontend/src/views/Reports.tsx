import { useState } from 'react';
import { Button, Row } from 'react-bootstrap';
import AddReportForm from '../components/addReport/AddReportForm';
import SearchReportsForm from '../components/SearchReportsForm';
import ReportsTable from '../components/reports/ReportsTable';
import { SearchReportParams } from '../types/reports';

const Reports = () => {
  const [form, setForm] = useState<SearchReportParams>({ from: '', to: '' });
  const [showAddForm, setShowAddForm, toggleShowFormState = () => setShowAddForm(!showAddForm)] = useState(false);

  return (
    <div style={{ padding: '10px' }}>
      <Row>
        <h3>
          Attack reports
        </h3>
      </Row>
      <SearchReportsForm onSubmit={setForm} form={form} container={Row} />
      <Row>
        <div>
          <Button onClick={toggleShowFormState} variant="light">Add new</Button>
          <ReportsTable form={form} />
        </div>
      </Row>
      {showAddForm &&
        <AddReportForm
          show={showAddForm}
          onClose={toggleShowFormState}
        />
      }
    </div>
  );
};

export default Reports;
