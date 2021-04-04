import React, { useState } from 'react';
import BootstrapTable, { ColumnDescription, ExpandRowProps } from 'react-bootstrap-table-next';
import AbortiveTreatmentsTable from '../AbortiveTreatmentsTable';
import { JournalMedical, CheckCircle } from 'react-bootstrap-icons';
import { AbortiveTreatment } from '../../types/reports';
import { useQuery } from 'react-query';
import { ReportsService } from '../../services/reports.service';
import paginationFactory, { PaginationListStandalone, PaginationProvider } from 'react-bootstrap-table2-paginator';
import { Pageable } from '../../types/common';

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
  renderer: (row: any) => <AbortiveTreatmentsTable list={row.usedAbortiveTreatments} />,
  onlyOneExpanding: true,
};


const ReportsTable = React.memo((props: any) => {
  const [pageable, setPageable] = useState<Pageable>({ page: 0, size: 10, sort: 'started', direction: 'asc', totalElements: 0 })

  const query: any = useQuery([
    'getReports',
    [props.form.from, props.form.to, pageable.page, pageable.sort, pageable.size],
  ], () => ReportsService.getReports({ pageable, form: props.form }));

  const handleTableChange = (_: string, { page, sizePerPage, sortField, sortOrder }: any) => {
    setPageable({ ...pageable, page, size: sizePerPage, sort: sortField, direction: sortOrder });
  };

  const paginationOption = {
    totalSize: query.data?.totalElements,
    sizePerPage: pageable.size,
    page: pageable.page,
    pageStartIndex: 0,
    custom: true,
    hideSizePerPage: false,
  };

  return (
    <PaginationProvider pagination={paginationFactory(paginationOption)}>
      {({ paginationProps, paginationTableProps }: any) => (
        <>
          <PaginationListStandalone {...paginationProps} />
          <BootstrapTable
            remote
            onTableChange={handleTableChange}
            keyField="attackId"
            data={query.data?.content ?? []}
            columns={columns}
            sort={{ dataField: pageable.sort, order: pageable.direction }}
            expandRow={expandRow}
            {...paginationTableProps}
          />
        </>
      )}
    </PaginationProvider>
  );
});

export default ReportsTable;
