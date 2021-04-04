export type Pageable = {
  page: number;
  size: number;
  sort: string;
  direction: string;
  totalElements: number;
}