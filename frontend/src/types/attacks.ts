export type Attack = {
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
