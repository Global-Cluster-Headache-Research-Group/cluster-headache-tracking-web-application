import axios from "axios";
import { Attack } from "../types/attacks";
import { Pageable } from "../types/common";

type AttacksReponse = {
  content: Attack[];
  totalElements: number;
}

type NewAttack = {
  started: string;
  stopped: string;
  comments?:
  string;
  maxPainLevel:
  number;
  whileAsleep: boolean;
}

export class AttacksService {
  private static ATTACKS_URL = 'http://localhost:8080/attacks';
  private static isoStringOrUndefined = (date?: string) => date ? new Date(date).toISOString() : undefined;

  static async getAttacks(pageable: Pageable, form: { from?: string, to?: string }): Promise<AttacksReponse> {
    const { data: {
      content,
      totalElements,
    } } = await axios.get<AttacksReponse>(AttacksService.ATTACKS_URL, {
      params: {
        ...pageable,
        from: this.isoStringOrUndefined(form.from),
        to: this.isoStringOrUndefined(form.to),
        sort: `${pageable.sort},${pageable.direction}`,
      },
    });
    return { content, totalElements };
  }

  static async addAttack(attack: NewAttack): Promise<void> {
    await axios.post(AttacksService.ATTACKS_URL, {
      ...attack,
      // FIXME: this is temporary until we will have authorization
      patientId: 1,
    });
  }
}