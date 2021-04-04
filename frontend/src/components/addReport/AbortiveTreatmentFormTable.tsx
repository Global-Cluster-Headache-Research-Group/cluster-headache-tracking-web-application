import { useEffect, useState } from 'react';
import BootstrapTable from 'react-bootstrap-table-next';
import cellEditFactory, { Type } from 'react-bootstrap-table2-editor';
import { XCircle } from 'react-bootstrap-icons';
import { last } from 'lodash/fp';
import { AbortiveTreatmentType } from '../../types/reports';
import { Button, Row } from 'react-bootstrap';

type Props = {
  onChange(list: UsedTreatment[]): void;
  abortiveTreatmentTypes: AbortiveTreatmentType[];
}

export type UsedTreatment = {
  id: number;
  usedTreatmentId: string;
  doze: string;
  successful: string;
}

const createColDefs = (onRemove: (id: number) => void, selectTypes: AbortiveTreatmentType[]): any => [
  {
    dataField: 'usedTreatmentId',
    text: 'Treatment',
    formatter: (usedTreatmentId: number) => {
      const type = selectTypes.find(t => t.id === +usedTreatmentId);
      return type ? `${type.name}, ${type.units}` : 'None';
    },
    editor: {
      type: Type.SELECT,
      options: selectTypes.map(t => ({ value: t.id, label: t.name })),
    },
  },
  {
    dataField: 'doze',
    text: 'Doze',
  },
  {
    dataField: 'successful',
    text: 'Successful',
    editor: {
      type: Type.CHECKBOX,
      value: 'true:false'
    },
  },
  {
    dataField: 'delete',
    text: '',
    isDummyField: true,
    headerStyle: { width: '50px' },
    style: { textAlign: 'center', width: '50px' },
    formatter: (cell: any, row: UsedTreatment) => (
      <XCircle color="#dc3545" onClick={() => onRemove(row.id)} />
    ),
    editable: false,
  },
];

const AbortiveTreatmentFormTable = (props: Props) => {
  const getId = (l: Array<{ id: number }>): number => (last(l)?.id ?? 0) + 1;

  const [list, changeList] = useState<UsedTreatment[]>([]);

  useEffect(() => props.onChange(list), [list]);

  const handleAddNewRow = () => changeList(l => [...l, { id: getId(l), usedTreatmentId: '', doze: '0', successful: 'true' }]);
  const handleRowEdit = (oldV: string, newV: string, row: UsedTreatment) => changeList(l => l.map((t: any) => t.id === row.id ? row : t))
  const handleRowRemove = (id: number) => changeList(l => l.filter((t: any) => t.id !== id));

  return (
    <>
      <BootstrapTable
        keyField="id"
        columns={createColDefs(handleRowRemove, props.abortiveTreatmentTypes)}
        data={list}
        cellEdit={cellEditFactory({
          mode: 'click',
          blurToSave: true,
          afterSaveCell: handleRowEdit,
        })}
      />
      <Row>
        <Button onClick={handleAddNewRow} block variant="link">Add used treatment</Button>
      </Row>
    </>
  );
};

export default AbortiveTreatmentFormTable;
