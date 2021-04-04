import { useFormik } from 'formik';
import { Button, Form } from 'react-bootstrap';
import { SearchReportParams } from '../types/reports';
import DatePicker from './form/DatePicker';

type Props = {
  form: SearchReportParams;
  onSubmit(form: SearchReportParams): void;
  container: React.ElementType;
}

const SearchReportsForm = ({ onSubmit, form, container: Container }: Props) => {
  const {
    handleChange,
    values,
    handleSubmit,
    setFieldValue,
  } = useFormik({
    initialValues: form,
    onSubmit: onSubmit,
  });

  return (
    <Form onSubmit={handleSubmit}>
      <Container>
        <Form.Group style={{ marginRight: '20px' }}>
          <DatePicker
            name="from"
            label="From date"
            value={values.from}
            onDateChange={handleChange}
            onDateClear={() => setFieldValue('from', '')}
            prependLabel
          />
        </Form.Group>
        <Form.Group style={{ marginRight: '20px' }}>
          <DatePicker
            name="to"
            label="To date"
            value={values.to}
            onDateChange={handleChange}
            onDateClear={() => setFieldValue('to', '')}
            prependLabel
          />
        </Form.Group>
        <Form.Group>
          <Button variant="primary" type="submit">
            Search
          </Button>
        </Form.Group>
      </Container>
    </Form>
  );
}

export default SearchReportsForm;