import React, { useState } from 'react';
import { Button, Col, Form, Modal, Row, Spinner } from 'react-bootstrap';
import { NewReport } from '../../services/reports.service';
import { AbortiveTreatmentType } from '../../types/reports';
import DatePicker from '../form/DatePicker';
import AbortiveTreatmentFormTable, { UsedTreatment } from './AbortiveTreatmentFormTable';

type Props = {
  show: boolean;
  onClose(): void;
  onAdd(newReport: NewReport): void;
  abortiveTreatmentsTypes: AbortiveTreatmentType[];
}

const AddReportForm = (props: Props) => {
  // FIXME: add started > stopped validation
  const [started, setStarted] = useState('');
  const [startedTime, setStartedTime] = useState('');
  const [stopped, setStopped] = useState('');
  const [stoppedTime, setStoppedTime] = useState('');
  const [whileAsleep, setWhileAsleep] = useState(false);
  const [maxPainLevel, setMaxPainLevel] = useState(1);
  const [comments, setComments] = useState('');
  const [isPending, setPending] = useState(false);
  const [usedTreatments, setUsedTreatments] = useState<NewReport['usedTreatments']>([]);

  const handleFormSubmit = async (e: any) => {
    e.preventDefault();
    setPending(true);
    props.onAdd({
      started: new Date(`${started} ${startedTime}`).toISOString(),
      stopped: new Date(`${stopped} ${stoppedTime}`).toISOString(),
      comments,
      whileAsleep,
      maxPainLevel,
      usedTreatments,
    });
    setPending(false);
    props.onClose();
  };

  const handleUsedTreatmentsChange = (l: UsedTreatment[]) => setUsedTreatments(l.map(i => ({
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
      <Form onSubmit={handleFormSubmit}>
        <Modal.Body>
          <h6>Attack details</h6>
          <Form.Group>
            <Row>
              <Col>
                <DatePicker
                  label="Started date"
                  value={started}
                  onDateChange={e => setStarted(e.target.value)}
                  onDateClear={() => setStarted('')}
                  required
                />
              </Col>
              <Col>
                <DatePicker
                  label="Started time"
                  value={startedTime}
                  onDateChange={e => setStartedTime(e.target.value)}
                  onDateClear={() => setStartedTime('')}
                  required
                  type="time"
                />
              </Col>
            </Row>
          </Form.Group>
          <Form.Group>
            <Row>
              <Col>
                <DatePicker
                  label="Stopped date"
                  value={stopped}
                  onDateChange={e => setStopped(e.target.value)}
                  onDateClear={() => setStopped('')}
                  required
                />
              </Col>
              <Col>
                <DatePicker
                  label="Stopped time"
                  value={stoppedTime}
                  onDateChange={e => setStoppedTime(e.target.value)}
                  onDateClear={() => setStoppedTime('')}
                  required
                  type="time"
                />
              </Col>
            </Row>
          </Form.Group>
          <Form.Group>
            <Form.Label>Max pain level <strong>{maxPainLevel}</strong></Form.Label>
            <Form.Control
              type="range"
              min={1}
              max={10}
              value={maxPainLevel}
              onChange={e => setMaxPainLevel(+e.target.value)}
              style={{ color: 'red' }}
            />
          </Form.Group>
          <Form.Group>
            <Form.Check
              checked={whileAsleep}
              onChange={() => setWhileAsleep(!whileAsleep)}
              type="checkbox"
              label="While asleep"
            />
          </Form.Group>
          <h6>Used abortive treatments</h6>
          <AbortiveTreatmentFormTable
            onChange={handleUsedTreatmentsChange}
            abortiveTreatmentTypes={props.abortiveTreatmentsTypes}
          />
          <h6>Comments</h6>
          <Form.Group>
            <Form.Control as="textarea" rows={5} value={comments} onChange={e => setComments(e.target.value)} />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button type="submit" block disabled={isPending}>
            {isPending ? <Spinner animation="border" /> : 'Add'}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
};

export default AddReportForm;