import React, { createContext, useCallback, useEffect, useState } from 'react';
import { Pageable } from '../types/common';
import { Attack } from '../types/attacks';
import { AttacksService } from '../services/attacks.service';

export type AttacksContextType = {
  attacks: Attack[];
  form: Form;
  pageable: Pageable;
  totalElements?: number;
  setForm(form: Form): void;
  setPageable(pageable: Pageable): void;
  submitForm(): void;
}

type Form = {
  from?: string;
  to?: string;
}

export const AttacksContext = createContext<AttacksContextType>({} as any);

export const AttacksContextProvider = (props: any) => {
  const [attacks, setAttacks] = useState<Attack[]>([]);
  const [form, setForm] = useState<{ from?: string; to?: string }>({ from: '', to: '' });
  const [pageable, setPageable] = useState<Pageable>({ page: 0, size: 10, sort: 'started', direction: 'asc' })
  const [totalElements, setTotalElements] = useState<number | undefined>();


  const submitForm = useCallback(async () => {
    const { content, totalElements } = await AttacksService.getAttacks(pageable, form);
    setAttacks(content);
    setTotalElements(totalElements);
  }, [form, pageable]);

  useEffect(() => {
    submitForm();
  }, [pageable]);

  const contextValue = {
    attacks,
    form,
    pageable,
    totalElements,
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
