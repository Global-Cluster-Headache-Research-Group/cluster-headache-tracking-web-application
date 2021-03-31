import React, { useState } from 'react';
import { Button, Col, Form, Modal, Row, Spinner } from 'react-bootstrap';
import { AttacksService } from '../services/attacks.service';
import DatePicker from './form/DatePicker';

type Props = {
  show: boolean;
  handleClose(): void;
}

const AttackForm = (props: Props) => {
  // FIXME: add started > stopped validation
  const [started, setStarted] = useState('');
  const [startedTime, setStartedTime] = useState('');
  const [stopped, setStopped] = useState('');
  const [stoppedTime, setStoppedTime] = useState('');
  const [whileAsleep, setWhileAsleep] = useState(false);
  const [maxPainLevel, setMaxPainLevel] = useState(1);
  const [comments, setComments] = useState('');
  const [isPending, setPending] = useState(false);

  const handleFormSubmit = async (e: any) => {
    e.preventDefault();
    setPending(true);
    await AttacksService.addAttack({
      started: new Date(`${started} ${startedTime}`).toISOString(),
      stopped: new Date(`${stopped} ${stoppedTime}`).toISOString(),
      comments,
      whileAsleep,
      maxPainLevel,
    });
    setPending(false);
    props.handleClose();
  };

  return (
    <Modal
      show={props.show}
      onHide={props.handleClose}
      backdrop="static"
      keyboard={false}
    >
      <Modal.Header closeButton>
        <Modal.Title>Add attack</Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleFormSubmit}>
        <Modal.Body>
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
          <Form.Group controlId="formBasicRange">
            <Form.Label>Max pain level <strong>{maxPainLevel}</strong></Form.Label>
            <Form.Control
              type="range"
              min={1}
              max={10}
              value={maxPainLevel}
              onChange={e => setMaxPainLevel(+e.target.value)}
            />
          </Form.Group>
          <Form.Check
            checked={whileAsleep}
            onChange={() => setWhileAsleep(!whileAsleep)}
            type="checkbox"
            label="While asleep"
          />
          <Form.Group controlId="exampleForm.ControlTextarea1">
            <Form.Label>Comments</Form.Label>
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

export default AttackForm;