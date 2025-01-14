import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { API_CONTEXT } from '../config';

export default class ReportDatasetColumnController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<ANY_OBJECT>>(
      API_CONTEXT + '/report/reportDatasetColumn/list',
      params,
      httpOptions,
    );
  }

  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<ANY_OBJECT>(
      API_CONTEXT + '/report/reportDatasetColumn/view',
      params,
      httpOptions,
    );
  }

  // static export (sender, params, fileName) {
  //   return super.download(API_CONTEXT + '/report/reportDatasetColumn/export', params, fileName);
  // }

  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/report/reportDatasetColumn/update', params, httpOptions);
  }
}
