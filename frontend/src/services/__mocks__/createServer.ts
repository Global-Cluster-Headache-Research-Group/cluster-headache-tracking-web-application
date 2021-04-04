import { createServer, Request } from 'miragejs';
import reports from './reports.json';
import treatmentTypes from './abortiveTreatmentTypes.json';

createServer({
  routes() {
    this.get('http://localhost:8080/reports', (_, { queryParams: { page, size, sort = 'started,asc', from, to }}: Request) => {
      const [sortField, direction] = sort.split(',');
      console.log(sortField, direction);
      const filteredReports = reports
        .sort((a, b) => {
          // @ts-ignore
          if ((a[sortField] > b[sortField]) ^ (direction === 'desc')) {
            return 1;
            // @ts-ignore
          } else if ((a[sortField] < b[sortField]) ^ (direction === 'desc')) {
            return -1;
          } else {
            return 0;
          }
        })
        .filter(e => {
          if (!from && !to) {
            return true;
          } else if (!from) {
            return e.started <= to;
          } else if (!to) {
            return e.started >= from;
          } else {
            return e.started >= from && e.started <= to;
          }
        });
      return {
        content: filteredReports.slice(+page * +size, +page * +size + +size),
        totalElements: filteredReports.length,
      };
    });
    this.get('http://localhost:8080/abortive-treatment-types', () => treatmentTypes)
  },
});
