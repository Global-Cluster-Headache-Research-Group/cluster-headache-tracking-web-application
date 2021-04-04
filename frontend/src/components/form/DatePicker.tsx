import { ChangeEvent } from 'react';
import { Button, Form, InputGroup } from 'react-bootstrap';
import { XCircle } from 'react-bootstrap-icons';

type Props = {
  name: string;
  label: string;
  value?: string;
  onDateChange(e: ChangeEvent<any>): void;
  onDateClear(): void;
  prependLabel?: boolean;
  required?: boolean;
  type?: 'time' | 'date';
}

const DatePicker = (props: Props) => (
  <>
    {!props.prependLabel && <Form.Label>{props.label}</Form.Label>}
    <InputGroup>
      {props.prependLabel && (
        <InputGroup.Prepend>
          <InputGroup.Text>{props.label}</InputGroup.Text>
        </InputGroup.Prepend>
      )}
      <Form.Control
        name={props.name}
        type={props.type ?? 'date'}
        value={props.value}
        onChange={props.onDateChange}
        required={props.required}
      />
      <InputGroup.Append>
        {props.value && <Button onClick={props.onDateClear}><XCircle /></Button>}
      </InputGroup.Append>
    </InputGroup>
  </>
);

export default DatePicker;
