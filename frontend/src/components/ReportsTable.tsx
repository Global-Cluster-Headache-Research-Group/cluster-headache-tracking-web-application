import React, { useContext, useState } from 'react';
import BootstrapTable, { ColumnDescription, ExpandRowProps, PaginationOptions } from 'react-bootstrap-table-next';
import { Button, Form, Row } from 'react-bootstrap';
import paginationFactory, { PaginationListStandalone, PaginationProvider } from 'react-bootstrap-table2-paginator';
import { ReportsContext, ReportsContextType } from '../context/ReportsContext';
import DatePicker from './form/DatePicker';
import AbortiveTreatmentsTable from './AbortiveTreatmentsTable';
import { JournalMedical, CheckCircle } from 'react-bootstrap-icons';
import { AbortiveTreatment } from '../types/reports';
import AddReportForm from './addReport/AddReportForm';

const columns: ColumnDescription[] = [
  {
    dataField: 'details',
    text: '',
    formatter: () => <JournalMedical />,
    isDummyField: true,
    style: { textAlign: 'center' },
    headerStyle: { width: '40px' },
  },
  {
    dataField: 'started',
    text: 'Started',
    sort: true,
    formatter: v => new Date(v).toLocaleString(),
  },
  {
    dataField: 'stopped',
    text: 'Stopped',
    sort: true,
    formatter: v => new Date(v).toLocaleString(),
  },
  {
    dataField: 'maxPainLevel',
    text: 'Pain',
    sort: true,
    style: { textAlign: 'center' },
    headerStyle: { width: '50px' },
  },
  {
    dataField: 'whileAsleep',
    text: 'While Asleep',
    headerStyle: { width: '70px' },
    formatter: (v: boolean) => v ? <CheckCircle /> : '',
  },
  {
    dataField: 'comments',
    text: 'Comments',
  },
  {
    dataField: 'usedAbortiveTreatments',
    text: 'With abortive threatment',
    // @ts-ignore
    formatter: (v: AbortiveTreatment[]) => v.length ? <CheckCircle /> : '',
    style: { textAlign: 'center' }
  },
];

const expandRow: ExpandRowProps<AbortiveTreatment> = {
  renderer: (row: any) => <AbortiveTreatmentsTable list={row.usedAbortiveTreatments}/>,
  onlyOneExpanding: true,
};

const ReportsTable = () => {
  const {
    reports,
    form,
    pageable,
    totalElements,
    setForm,
    setPageable,
    onSearch,
    onSubmit,
    abortiveTreatmentsTypes,
  } = useContext<ReportsContextType>(ReportsContext);
  const [showAddForm, setShowAddForm] = useState(false);

  const paginationOption: PaginationOptions = {
    totalSize: totalElements,
    sizePerPage: pageable.size,
    page: pageable.page,
    pageStartIndex: 0,
    custom: true,
    hideSizePerPage: false,
  };

  const handleDateChange = (name: string) => (e: any) => setForm({ ...form, [name]: e.target.value });
  const handleClearDate = (name: string) => () => setForm({ ...form, [name]: '' });
  const handleTableChange = (_: string, { page, sizePerPage, sortField, sortOrder }: any) => {
    setPageable({ ...pageable, page, size: sizePerPage, sort: sortField, direction: sortOrder });
  };

  return (
    <PaginationProvider pagination={paginationFactory(paginationOption)}>
      {({ paginationProps, paginationTableProps }: any) => (
        <div style={{ padding: '10px' }}>
          <Row>
            <h3>
              Reports
            </h3>
          </Row>
          <Row>
            <Form inline>
              <Form.Group style={{ marginRight: '20px' }}>
                <DatePicker
                  label="From date"
                  value={form.from}
                  onDateChange={handleDateChange('from')}
                  onDateClear={handleClearDate('from')}
                  prependLabel
                />
              </Form.Group>
              <Form.Group style={{ marginRight: '20px' }}>
                <DatePicker
                  label="To date"
                  value={form.to}
                  onDateChange={handleDateChange('to')}
                  onDateClear={handleClearDate('to')}
                  prependLabel
                />
              </Form.Group>
              <Form.Group>
                <Button variant="primary" onClick={onSearch} >
                  Search
                </Button>
              </Form.Group>
            </Form>
          </Row>
          <br />
          <Row>
            <div>
              <Button onClick={() => setShowAddForm(true)} variant="light">Add new</Button>
              <PaginationListStandalone {...paginationProps} />
              <BootstrapTable
                remote
                onTableChange={handleTableChange}
                keyField="attackId"
                data={reports ?? []}
                columns={columns}
                sort={{ dataField: pageable.sort, order: pageable.direction }}
                expandRow={ expandRow }
                {...paginationTableProps}
              />
            </div>
          </Row>

          {showAddForm &&
            <AddReportForm
              show={showAddForm}
              onClose={() => setShowAddForm(false)}
              onAdd={onSubmit}
              abortiveTreatmentsTypes={abortiveTreatmentsTypes}
            />
          }
        </div>
      )}
    </PaginationProvider>
  );
};

export default ReportsTable;
