import React, { createContext, useEffect, useState } from 'react';
import { Pageable } from '../types/common';
import { AbortiveTreatmentType, Report } from '../types/reports';
import { NewReport, ReportsService } from '../services/reports.service';

export type ReportsContextType = {
  reports: Report[];
  form: Form;
  pageable: Pageable;
  totalElements?: number;
  setForm(form: Form): void;
  setPageable(pageable: Pageable): void;
  onSubmit(newReport: NewReport): void;
  onSearch(): void;
  abortiveTreatmentsTypes: AbortiveTreatmentType[];
}

type Form = {
  from?: string;
  to?: string;
}

export const ReportsContext = createContext<ReportsContextType>({} as any);

export const ReportsContextProvider = (props: any) => {
  const [reports, setReports] = useState<Report[]>([]);
  const [form, setForm] = useState<{ from?: string; to?: string }>({ from: '', to: '' });
  const [pageable, setPageable] = useState<Pageable>({ page: 0, size: 10, sort: 'started', direction: 'asc' })
  const [totalElements, setTotalElements] = useState<number | undefined>();
  const [abortiveTreatmentsTypes, setAbortiveTreatmentsTypes] = useState<AbortiveTreatmentType[]>([]);

  useEffect(() => {
    ReportsService.getAbortiveTreatmentTypes().then(setAbortiveTreatmentsTypes);
  }, []);

  const onSearch = async () => {
    const { content, totalElements } = await ReportsService.getReports(pageable, form);
    setReports(content);
    setTotalElements(totalElements);
  };

  const onSubmit = (newReport: NewReport) => ReportsService.addReport(newReport);

  useEffect(() => {
    onSearch();
  }, [pageable]);

  const contextValue = {
    reports,
    form,
    pageable,
    totalElements,
    setForm,
    setPageable,
    onSearch,
    abortiveTreatmentsTypes,
    onSubmit,
  }
  return (
    <ReportsContext.Provider value={contextValue}>
      {props.children}
    </ReportsContext.Provider>
  );
};
