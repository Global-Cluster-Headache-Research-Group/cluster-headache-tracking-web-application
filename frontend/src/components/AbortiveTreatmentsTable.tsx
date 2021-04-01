import React from 'react';
import { EmojiAngry, EmojiLaughing } from 'react-bootstrap-icons';
import BootstrapTable, { ColumnDescription } from 'react-bootstrap-table-next';
import { AbortiveTreatment } from '../types/reports'

type Props = {
  list: AbortiveTreatment[];
}

const columns: ColumnDescription[] = [
  {
    dataField: 'name',
    text: 'Name',
  },
  {
    dataField: 'tradeName',
    text: 'Trade name',
  },
  {
    dataField: 'doze',
    text: 'Doze',
  },
  {
    dataField: 'units',
    text: 'Units',
  },
  {
    dataField: 'successful',
    text: 'Successful',
    style: { textAlign: 'center' },
    formatter: (v: boolean) => v
      ? <EmojiLaughing color="#198754" />
      : <EmojiAngry color="#dc3545" />,
  },
];

const AbortiveTreatmentsTable = (props: Props) => {
  return (
    <div>
      <h6>Used abortive treatments</h6>
      <BootstrapTable
        keyField="name"
        columns={columns}
        data={props.list}
      />
    </div>
  )
};

export default AbortiveTreatmentsTable;
