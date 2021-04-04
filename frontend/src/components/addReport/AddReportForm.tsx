import { useFormik } from 'formik';
import { Button, Col, Form, Modal, Row, Spinner } from 'react-bootstrap';
import { useMutation, useQuery } from 'react-query';
import { NewReport, ReportsService } from '../../services/reports.service';
import DatePicker from '../form/DatePicker';
import AbortiveTreatmentFormTable, { UsedTreatment } from './AbortiveTreatmentFormTable';
import { format, addHours } from 'date-fns';
import { queryClient } from '../../services/queryClient';


type Props = {
  show: boolean;
  onClose(): void;
}

const AddReportForm = (props: Props) => {
  const nowDate = new Date();
  const inThreeHoursDate = addHours(nowDate, 3);

  const mutation = useMutation(ReportsService.addReport, {
    onSuccess: () => queryClient.invalidateQueries('getReports'),
  });

  const {
    handleChange,
    values,
    handleSubmit,
    setFieldValue,
    isSubmitting,
  } = useFormik({
    initialValues: {
      started: format(nowDate, 'yyyy-MM-dd'),
      startedTime: format(nowDate, 'HH:mm'),
      stopped: format(inThreeHoursDate, 'yyyy-MM-dd'),
      stoppedTime: format(inThreeHoursDate, 'HH:mm'),
      comments: '',
      whileAsleep: false,
      isPending: false,
      maxPainLevel: 1,
      usedTreatments: [],
    },
    onSubmit: ({ started, stopped, startedTime, stoppedTime, comments, whileAsleep, maxPainLevel, usedTreatments }, actions) => {
      mutation.mutate({
        comments,
        whileAsleep,
        maxPainLevel,
        usedTreatments,
        started: new Date(`${started} ${startedTime}`).toISOString(),
        stopped: new Date(`${stopped} ${stoppedTime}`).toISOString(),
      });
      actions.resetForm();
      actions.setSubmitting(false);
      props.onClose();
    },
  });

  const abortiveTreatmentTypesQuery: any = useQuery('abortiveTreatments', ReportsService.getAbortiveTreatmentTypes);

  const handleUsedTreatmentsChange = (l: UsedTreatment[]) => setFieldValue('usedTreatment', l.map(i => ({
    id: +i.usedTreatmentId,
    doze: +i.doze,
    successful: Boolean(i.successful),
  })));

  return (
    <Modal
      show={props.show}
      onHide={props.onClose}
      backdrop="static"
      keyboard={false}
    >
      <Modal.Header closeButton>
        <Modal.Title>Add new report</Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          <h6>Attack details</h6>
          <Form.Group>
            <Row>
              <Col>
                <DatePicker
                  required
                  name="started"
                  label="Started date"
                  value={values.started}
                  onDateChange={handleChange}
                  onDateClear={() => setFieldValue('started', '')}
                />
              </Col>
              <Col>
                <DatePicker
                  required
                  name="startedTime"
                  label="Started time"
                  value={values.startedTime}
                  onDateChange={handleChange}
                  onDateClear={() => setFieldValue('startedTime', '')}
                  type="time"
                />
              </Col>
            </Row>
          </Form.Group>
          <Form.Group>
            <Row>
              <Col>
                <DatePicker
                  required
                  name="stopped"
                  label="Stopped date"
                  value={values.stopped}
                  onDateChange={handleChange}
                  onDateClear={() => setFieldValue('stopped', '')}
                />
              </Col>
              <Col>
                <DatePicker
                  required
                  name="stoppedTime"
                  label="Stopped time"
                  value={values.stoppedTime}
                  onDateChange={handleChange}
                  onDateClear={() => setFieldValue('stoppedTime', '')}
                  type="time"
                />
              </Col>
            </Row>
          </Form.Group>
          <Form.Group>
            <Form.Label>Max pain level <strong>{values.maxPainLevel}</strong></Form.Label>
            <Form.Control
              name="maxPainLevel"
              type="range"
              min={1}
              max={10}
              value={values.maxPainLevel}
              onChange={handleChange}
              style={{ color: 'red' }}
            />
          </Form.Group>
          <Form.Group>
            <Form.Check
              name="whileAsleep"
              checked={values.whileAsleep}
              onChange={handleChange}
              type="checkbox"
              label="While asleep"
            />
          </Form.Group>
          <h6>Used abortive treatments</h6>
          <AbortiveTreatmentFormTable
            onChange={handleUsedTreatmentsChange}
            abortiveTreatmentTypes={abortiveTreatmentTypesQuery.data ?? []}
          />
          <h6>Comments</h6>
          <Form.Group>
            <Form.Control name="comments" as="textarea" rows={5} value={values.comments} onChange={handleChange} />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button type="submit" block disabled={isSubmitting}>
            {isSubmitting ? <Spinner animation="border" /> : 'Add'}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
};

export default AddReportForm;