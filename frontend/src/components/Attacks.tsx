import React, { useContext, useState } from 'react';
import BootstrapTable, { ColumnDescription, PaginationOptions } from 'react-bootstrap-table-next';
import { Button, Form, Row } from 'react-bootstrap';
import paginationFactory, { PaginationListStandalone, PaginationProvider } from 'react-bootstrap-table2-paginator';
import { AttacksContext, AttacksContextType } from '../context/AttacksContext';
import AttackForm from './AddAttackForm';
import DatePicker from './form/DatePicker';

const columns: ColumnDescription[] = [
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
    text: 'Pain level',
    sort: true,
  },
  {
    dataField: 'whileAsleep',
    text: 'While Asleep',
  },
  {
    dataField: 'comments',
    text: 'Comments',
  },
];

const AttacksTable = () => {
  const {
    attacks,
    form,
    pageable,
    totalElements,
    setForm,
    setPageable,
    submitForm,
  } = useContext<AttacksContextType>(AttacksContext);
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
              Attacks
            </h3>
          </Row>
          <Row>
            <Form inline>
              <Form.Group style={{ marginRight: '20px' }}>
                <DatePicker
                  label="Drom date"
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
                <Button variant="primary" onClick={submitForm} >
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
                keyField='started'
                data={attacks ?? []}
                columns={columns}
                sort={{ dataField: pageable.sort, order: pageable.direction }}
                {...paginationTableProps}
              />
            </div>
          </Row>
          <AttackForm
            show={showAddForm}
            handleClose={() => setShowAddForm(false)}
          />
        </div>
      )}
    </PaginationProvider>
  );
};

export default AttacksTable;
