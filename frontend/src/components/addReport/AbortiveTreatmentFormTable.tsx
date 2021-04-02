import { useEffect, useState } from 'react';
import BootstrapTable from 'react-bootstrap-table-next';
import cellEditFactory, { Type } from 'react-bootstrap-table2-editor';
import { XCircle } from 'react-bootstrap-icons';
import { isEqual, omit, last } from 'lodash/fp';
import { AbortiveTreatmentType } from '../../types/reports';

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

const initialItem = {
  id: 0,
  usedTreatmentId: '',
  doze: '0',
  successful: 'true',
};

const AbortiveTreatmentFormTable = (props: Props) => {
  const omitId = omit('id');
  const isEqualWithoutId = (oldV: any, newV: any): boolean => isEqual(omitId(oldV), omitId(newV));
  const getId = (l: Array<{ id: number }>): number => (last(l)?.id ?? 0) + 1;

  const [list, changeList] = useState<UsedTreatment[]>([{ ...initialItem }]);

  useEffect(() => props.onChange(list.filter(i => !isEqualWithoutId(i, initialItem))), [list]);

  const addNewRow = () => changeList(l => [...l, { ...initialItem, id: getId(l) }]);

  const handleRowEdit = (oldV: string, newV: string, row: UsedTreatment) => {
    if (!isEqualWithoutId(initialItem, row) && row.id >= last(list)!.id) {
      addNewRow();
    }
    changeList(l => l.map((t: any) => t.id !== row.id ? t : row));
  }

  const handleRowRemove = (id: number) => changeList(l => id === last(l)!.id
    ? l
    : l.filter((t: any) => t.id !== id),
  );

  return (
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
  );
};

export default AbortiveTreatmentFormTable;
