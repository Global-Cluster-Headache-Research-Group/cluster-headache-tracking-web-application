import axios from "axios";
import { AbortiveTreatmentType, Report, SearchReportParams } from "../types/reports";
import { Pageable } from "../types/common";

type ReportsResponse = {
  content: Report[];
  totalElements: number;
}

export type NewReport = {
  started: string;
  stopped: string;
  comments?:
  string;
  maxPainLevel:
  number;
  whileAsleep: boolean;
  usedTreatments: Array<{ id: number; doze: number; successful: boolean; }>;
}

export class ReportsService {
  // TODO: add global config
  private static REPORTS_BASE_URL = 'http://localhost:8080/reports';
  private static ABORTIVE_TREATMENTS_URL = 'http://localhost:8080/abortive-treatment-types';

  private static isoStringOrUndefined = (date?: string) => date ? new Date(date).toISOString() : undefined;

  static async getReports({ pageable, form }: { pageable: Pageable, form: SearchReportParams }): Promise<ReportsResponse> {
    const { data: {
      content,
      totalElements,
    } } = await axios.get<ReportsResponse>(ReportsService.REPORTS_BASE_URL, {
      params: {
        ...pageable,
        from: ReportsService.isoStringOrUndefined(form?.from),
        to: ReportsService.isoStringOrUndefined(form?.to),
        sort: `${pageable?.sort},${pageable?.direction}`,
      },
    });
    return { content, totalElements };
  }

  static async addReport(attack: NewReport): Promise<void> {
    await axios.post(ReportsService.REPORTS_BASE_URL, {
      ...attack,
      // FIXME: this is temporary until we will have authorization
      patientId: 1,
    });
  }

  static async getAbortiveTreatmentTypes(): Promise<AbortiveTreatmentType[]> {
    const { data } = await axios.get<AbortiveTreatmentType[]>(ReportsService.ABORTIVE_TREATMENTS_URL);
    return data;
  }
}