import React, { useContext } from 'react';
import BootstrapTable, { ColumnDescription } from 'react-bootstrap-table-next';
import { Button, Form, InputGroup, Row } from 'react-bootstrap';
import paginationFactory, { PaginationListStandalone, PaginationProvider } from 'react-bootstrap-table2-paginator';
import { AttacksContext, AttacksContextType } from '../context/AttacksContext';
import { XCircle } from 'react-bootstrap-icons';

const columns: ColumnDescription[] = [{
  dataField: 'started',
  text: 'Started',
  sort: true,
  formatter: v => new Date(v).toLocaleString(),
}, {
  dataField: 'stopped',
  text: 'Stopped',
  sort: true,
  formatter: v => new Date(v).toLocaleString(),
}, {
  dataField: 'maxPainLevel',
  text: 'Pain level',
  sort: true,
}, {
  dataField: 'patient.name',
  text: 'Patient',
}, {
  dataField: 'whileAsleep',
  text: 'While Asleep',
}, {
  dataField: 'comments',
  text: 'Comments',
}];

const AttacksTable = () => {
  const {
    attacks,
    form,
    pageable,
    dataLength,
    setForm,
    setPageable,
    submitForm,
  } = useContext<AttacksContextType>(AttacksContext);

  const handleDateChange = (name: string) => (e: any) => setForm({ ...form, [name]: e.target.value });
  const handleClearDate = (name: string) => () => setForm({ ...form, [name]: '' });

  const paginationOption = {
    totalSize: dataLength,
    sizePerPage: pageable.size,
    page: pageable.page,
    pageStartIndex: 0,
    showTotal: true,
  };

  const handleTableChange = (_: string, { page, sizePerPage, sortField, sortOrder }: any) => {
    setPageable({ ...pageable, page, size: sizePerPage, sort: sortField, direction: sortOrder });
  };

  return (
    <div style={{ padding: '10px' }}>
      <Row>
        <h3>
          Attacks
        </h3>
      </Row>
      <Row>
        <Form inline>
          <Form.Group style={{ marginRight: '20px' }}>
            <InputGroup>
              <InputGroup.Prepend>
                <InputGroup.Text>From date</InputGroup.Text>
              </InputGroup.Prepend>
              <Form.Control type="date" value={form.from} onChange={handleDateChange('from')}/>
              <InputGroup.Append>
                {form.from && <Button onClick={handleClearDate('from')}><XCircle /></Button>}
              </InputGroup.Append>
            </InputGroup>
          </Form.Group>
          <Form.Group style={{ marginRight: '20px' }}>
            <InputGroup>
              <InputGroup.Prepend>
                <InputGroup.Text>To date</InputGroup.Text>
              </InputGroup.Prepend>
              <Form.Control type="date" value={form.to} onChange={handleDateChange('to')}/>
              <InputGroup.Append>
                {form.to && <Button onClick={handleClearDate('to')}><XCircle /></Button>}
              </InputGroup.Append>
            </InputGroup>
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
        <PaginationProvider
          pagination={paginationFactory(paginationOption)}
        >
          {({ paginationProps, paginationTableProps }: any) => (
            <div>
              <PaginationListStandalone {...paginationProps} />
              <BootstrapTable
                remote
                {...paginationTableProps}
                onTableChange={handleTableChange}
                keyField='started'
                data={attacks ?? []}
                columns={columns}
                sort={{ dataField: pageable.sort, order: pageable.direction }}
              />
            </div>
          )
          }
        </PaginationProvider>
      </Row>
    </div>
  );
};

export default AttacksTable;
