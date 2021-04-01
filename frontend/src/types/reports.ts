export type Report = {
  attackId: string;
  started: Date;
  stopped: Date;
  maxPainLevel: number;
  whileAsleep: boolean;
  comments: string;
  usedAbortiveTreatments: AbortiveTreatment[];
}

export type AbortiveTreatment = {
  abortiveTreatmentName: string;
  abortiveTreatmentTradeName: string;
  abortiveTreatmentUnits: string;
  successful: boolean;
  doze: number;
}
