import React, { createContext, useCallback, useEffect, useState } from 'react';
import axios from 'axios';

export type AttacksContextType = {
  attacks: Attack[];
  form: Form;
  pageable: Pageable;
  dataLength?: number;
  setForm(form: Form): void;
  setPageable(pageable: Pageable): void;
  submitForm(): void;
}

type Form = {
  from?: string;
  to?: string;
}

export const AttacksContext = createContext<AttacksContextType>({} as any);

type Attack = {
  started: Date;
  stopped: Date;
  patient: {
    id: number;
    login: string;
    email: string;
    birthday: Date;
    name: string;
    gender: 1;
  },
  maxPainLevel: number;
  whileAsleep: boolean;
  comments: string;
}

type Pageable = {
  page: number;
  size: number;
  sort: string;
  direction: string;
}

export const AttacksContextProvider = (props: any) => {
  const [attacks, setAttacks] = useState<Attack[]>([]);
  const [form, setForm] = useState<{ from?: string; to?: string }>({ from: '', to: '' });
  const [pageable, setPageable] = useState<Pageable>({ page: 0, size: 5, sort: 'started', direction: 'asc' })
  const [dataLength, setDataLength] = useState<number | undefined>();

  const makeRequest = () => axios.get('http://localhost:8080/attacks', {
    params: {
      from: form.from ? new Date(form.from).toISOString() : undefined,
      to: form.to ? new Date(form.to).toISOString() : undefined,
      ...pageable,
      sort: `${pageable.sort},${pageable.direction}`,
    },
  });

  useEffect(() => {
    makeRequest().then((r) => {
      setAttacks(r.data.content);
      setDataLength(r.data.totalElements);
    });
  }, [pageable]);

  const submitForm = useCallback(() => makeRequest().then((r) => {
      setAttacks(r.data.content);
      setDataLength(r.data.totalElements);
    }), [form, pageable]);

  const contextValue = {
    attacks,
    form,
    pageable,
    dataLength,
    setForm,
    setPageable,
    submitForm,
  }
  return (
    <AttacksContext.Provider value={contextValue}>
      {props.children}
    </AttacksContext.Provider>
  );
};
