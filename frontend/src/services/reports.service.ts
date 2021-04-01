import axios from "axios";
import { Report } from "../types/reports";
import { Pageable } from "../types/common";

type ReportsResponse = {
  content: Report[];
  totalElements: number;
}

type NewReport = {
  started: string;
  stopped: string;
  comments?:
  string;
  maxPainLevel:
  number;
  whileAsleep: boolean;
}

export class ReportsService {
  // TODO: add global config
  private static REPORTS_BASE_URL = 'http://localhost:8080/reports';
  private static isoStringOrUndefined = (date?: string) => date ? new Date(date).toISOString() : undefined;

  static async getReports(pageable: Pageable, form: { from?: string, to?: string }): Promise<ReportsResponse> {
    const { data: {
      content,
      totalElements,
    } } = await axios.get<ReportsResponse>(ReportsService.REPORTS_BASE_URL, {
      params: {
        ...pageable,
        from: this.isoStringOrUndefined(form.from),
        to: this.isoStringOrUndefined(form.to),
        sort: `${pageable.sort},${pageable.direction}`,
      },
    });
    return { content, totalElements };
  }

  static async addAttack(attack: NewReport): Promise<void> {
    await axios.post(ReportsService.REPORTS_BASE_URL, {
      ...attack,
      // FIXME: this is temporary until we will have authorization
      patientId: 1,
    });
  }
}